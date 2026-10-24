package com.habilitesrandom.data;

import com.habilitesrandom.HabilitesRandom;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

/** Only class allowed to read or mutate persistent gameplay profiles on the logical server. */
public final class PlayerDataManager {
    private PlayerDataManager() {
    }

    public static PlayerProfileState state(MinecraftServer server) {
        if (server == null) {
            throw new IllegalStateException("Player data requested before the server started");
        }
        return server.getDataStorage().computeIfAbsent(PlayerProfileState.TYPE);
    }

    public static void prime(MinecraftServer server) {
        PlayerProfileState state = state(server);
        int migrated = state.migrateToCurrentSchema();
        HabilitesRandom.LOGGER.info("Loaded {} HabilitesRandom player profile(s)", state.size());
        if (migrated > 0) {
            HabilitesRandom.LOGGER.info("Migrated {} HabilitesRandom player profile(s) to schema {} without resetting profile data",
                    migrated, PlayerProfile.SCHEMA_VERSION);
        }
    }

    public static PlayerProfile get(MinecraftServer server, UUID playerId) {
        return state(server).get(playerId);
    }

    public static PlayerProfile initialize(ServerPlayer player) {
        MinecraftServer server = ServerRuntime.server();
        if (server == null || player == null) {
            return PlayerProfile.empty();
        }
        PlayerProfileState state = state(server);
        long now = System.currentTimeMillis();
        if (!state.contains(player.getUUID())) {
            state.put(player.getUUID(), PlayerProfile.empty(now));
        }
        return state.get(player.getUUID()).touch(now);
    }

    public static void update(MinecraftServer server, UUID playerId, PlayerProfile profile) {
        if (server != null) {
            state(server).put(playerId, profile.touch(System.currentTimeMillis()));
        }
    }

    public static void flush(MinecraftServer server) {
        if (server == null) {
            return;
        }
        try {
            server.getDataStorage().saveAndJoin();
        } catch (RuntimeException error) {
            HabilitesRandom.LOGGER.error("Failed to flush HabilitesRandom player profiles", error);
        }
    }
}
