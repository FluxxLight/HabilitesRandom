package com.habilitesrandom.player;

import com.habilitesrandom.HabilitesRandom;
import com.habilitesrandom.data.PlayerDataManager;
import com.habilitesrandom.data.ServerRuntime;
import com.habilitesrandom.network.ActionRequestPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Rate-limits and validates untrusted action packets. No gameplay side effects yet. */
public final class ServerActionGateway {
    private static final long MIN_INTERVAL_MILLIS = 250L;
    private static final Map<UUID, Long> LAST_REQUEST = new ConcurrentHashMap<>();

    private ServerActionGateway() {
    }

    public static void handle(ActionRequestPayload payload, ServerPlayer player) {
        if (payload == null || player == null) {
            return;
        }

        PlayerAuthorityGate.Action action;
        try {
            action = PlayerAuthorityGate.Action.valueOf(payload.action());
        } catch (IllegalArgumentException invalid) {
            HabilitesRandom.LOGGER.debug("Rejected unknown HabilitesRandom action '{}'", payload.action());
            return;
        }

        long now = System.currentTimeMillis();
        Long previous = LAST_REQUEST.putIfAbsent(player.getUUID(), now);
        if (previous != null) {
            if (now - previous < MIN_INTERVAL_MILLIS) {
                return;
            }
            LAST_REQUEST.put(player.getUUID(), now);
        }

        MinecraftServer server = ServerRuntime.server();
        if (server == null) {
            return;
        }
        PlayerAuthorityGate.Decision decision = PlayerAuthorityGate.validate(player,
                PlayerDataManager.get(server, player.getUUID()), action, payload.slot());
        if (!decision.allowed()) {
            HabilitesRandom.LOGGER.debug("Rejected {} from {}: {}", action, player.getGameProfile().name(),
                    decision.reason());
        }
    }

    public static void forget(UUID playerId) {
        LAST_REQUEST.remove(playerId);
    }
}
