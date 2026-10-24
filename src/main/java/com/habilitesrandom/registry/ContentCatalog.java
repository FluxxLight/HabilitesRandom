package com.habilitesrandom.registry;

import com.habilitesrandom.HabilitesRandom;
import com.habilitesrandom.api.CatalogEntry;
import com.habilitesrandom.api.ContentKind;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe, type-keyed catalog. The implementation is Core-owned but its buckets are defined by
 * {@link ContentKind}, so a Plus can add whole content families without modifying this class.
 */
public final class ContentCatalog {
    private static final Logger LOGGER = LoggerFactory.getLogger(HabilitesRandom.MOD_ID + "/catalog");

    private final Map<ContentKind<?>, Map<Identifier, CatalogEntry>> buckets = new ConcurrentHashMap<>();

    public <T extends CatalogEntry> boolean register(ContentKind<T> kind, T definition, String sourceAddon) {
        Objects.requireNonNull(kind, "kind");
        Objects.requireNonNull(definition, "definition");
        Objects.requireNonNull(definition.id(), "definition id");

        Map<Identifier, CatalogEntry> bucket = buckets.computeIfAbsent(kind, ignored -> new ConcurrentHashMap<>());
        CatalogEntry previous = bucket.putIfAbsent(definition.id(), definition);
        if (previous != null) {
            LOGGER.warn("Rejected duplicate {} '{}' from {} (already registered)", kind.type().getSimpleName(),
                    definition.id(), sourceAddon);
            return false;
        }

        LOGGER.info("Registered {} '{}' from {}", kind.type().getSimpleName(), definition.id(), sourceAddon);
        return true;
    }

    public <T extends CatalogEntry> Optional<T> find(ContentKind<T> kind, Identifier id) {
        if (kind == null || id == null) {
            return Optional.empty();
        }
        Map<Identifier, CatalogEntry> bucket = buckets.get(kind);
        if (bucket == null) {
            return Optional.empty();
        }
        CatalogEntry entry = bucket.get(id);
        if (entry == null || !kind.type().isInstance(entry)) {
            return Optional.empty();
        }
        return Optional.of(kind.type().cast(entry));
    }

    public <T extends CatalogEntry> List<T> snapshot(ContentKind<T> kind) {
        Objects.requireNonNull(kind, "kind");
        Map<Identifier, CatalogEntry> bucket = buckets.get(kind);
        if (bucket == null || bucket.isEmpty()) {
            return List.of();
        }
        List<T> result = new ArrayList<>(bucket.size());
        for (CatalogEntry entry : bucket.values()) {
            if (kind.type().isInstance(entry)) {
                result.add(kind.type().cast(entry));
            }
        }
        result.sort(Comparator.comparing(entry -> entry.id().toString()));
        return List.copyOf(result);
    }

    public int size(ContentKind<?> kind) {
        Map<Identifier, CatalogEntry> bucket = buckets.get(kind);
        return bucket == null ? 0 : bucket.size();
    }

    public int totalSize() {
        int total = 0;
        for (Map<Identifier, CatalogEntry> bucket : buckets.values()) {
            total += bucket.size();
        }
        return total;
    }
}
