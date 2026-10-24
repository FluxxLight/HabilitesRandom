package com.habilitesrandom.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.habilitesrandom.HabilitesRandom;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/** Atomic JSON persistence for local presentation preferences; never authoritative gameplay data. */
public final class ClientConfigStore {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path directory;
    private final Path file;
    private volatile ClientConfig config;

    public ClientConfigStore() {
        this(FabricLoader.getInstance().getConfigDir().resolve(HabilitesRandom.MOD_ID));
    }

    public ClientConfigStore(Path directory) {
        this.directory = directory;
        this.file = directory.resolve("client-settings.json");
        this.config = loadOrCreate();
    }

    public ClientConfig get() {
        return config;
    }

    private ClientConfig loadOrCreate() {
        ClientConfig loaded = new ClientConfig();
        try {
            Files.createDirectories(directory);
            if (Files.isRegularFile(file)) {
                try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                    ClientConfig fromDisk = GSON.fromJson(reader, ClientConfig.class);
                    if (fromDisk != null) {
                        loaded = fromDisk;
                    }
                } catch (JsonParseException broken) {
                    quarantineBrokenFile();
                }
            }
        } catch (IOException | RuntimeException error) {
            HabilitesRandom.LOGGER.warn("Could not read HabilitesRandom client settings", error);
        }
        loaded.sanitize();
        config = loaded;
        save();
        return loaded;
    }

    public void save() {
        ClientConfig current = config;
        if (current == null) {
            return;
        }
        current.sanitize();
        Path temporary = file.resolveSibling(file.getFileName() + ".tmp");
        try {
            Files.createDirectories(directory);
            try (Writer writer = Files.newBufferedWriter(temporary, StandardCharsets.UTF_8)) {
                GSON.toJson(current, writer);
            }
            try {
                Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ignored) {
                Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException | RuntimeException error) {
            HabilitesRandom.LOGGER.warn("Could not write HabilitesRandom client settings", error);
        }
    }

    private void quarantineBrokenFile() {
        try {
            Files.move(file, file.resolveSibling("client-settings.broken-" + System.currentTimeMillis() + ".json"),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException error) {
            HabilitesRandom.LOGGER.warn("Could not quarantine broken client settings", error);
        }
    }
}
