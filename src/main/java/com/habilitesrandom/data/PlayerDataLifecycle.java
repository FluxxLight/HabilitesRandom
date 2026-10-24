package com.habilitesrandom.data;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

/** Registers every persistence boundary: startup, join, logout and shutdown. */
public final class PlayerDataLifecycle {
    private PlayerDataLifecycle() {
    }

    public static void register() {
        ServerLifecycleEvents.SERVER_STARTED.register(ServerRuntime::started);
        ServerLifecycleEvents.SERVER_STOPPING.register(ServerRuntime::stopping);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (handler.getPlayer() != null) {
                PlayerProfileHooks.onJoin(handler.getPlayer());
            }
        });
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            if (handler.getPlayer() != null) {
                PlayerProfileHooks.onDisconnect(handler.getPlayer());
            } else {
                PlayerDataManager.flush(server);
            }
        });
    }
}
