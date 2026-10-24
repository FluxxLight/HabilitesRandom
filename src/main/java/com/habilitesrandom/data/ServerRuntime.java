package com.habilitesrandom.data;

import net.minecraft.server.MinecraftServer;

import java.util.concurrent.atomic.AtomicReference;

/** Holds the currently active logical server; cleared correctly between integrated-server worlds. */
public final class ServerRuntime {
    private static final AtomicReference<MinecraftServer> ACTIVE = new AtomicReference<>();

    private ServerRuntime() {
    }

    public static void started(MinecraftServer server) {
        ACTIVE.set(server);
        PlayerDataManager.prime(server);
    }

    public static void stopping(MinecraftServer server) {
        PlayerDataManager.flush(server);
        ACTIVE.compareAndSet(server, null);
    }

    public static MinecraftServer server() {
        return ACTIVE.get();
    }
}
