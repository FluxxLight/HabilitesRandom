package com.habilitesrandom.network;

import com.habilitesrandom.data.PlayerDataManager;
import com.habilitesrandom.data.ServerRuntime;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

/** Builds snapshots exclusively from server-owned saved data. */
public final class NetworkSync {
    private NetworkSync() {
    }

    public static void sendProfile(ServerPlayer player) {
        if (player == null || !ServerPlayNetworking.canSend(player, ProfileSyncPayload.TYPE)) {
            return;
        }
        MinecraftServer server = ServerRuntime.server();
        if (server == null) {
            return;
        }
        ServerPlayNetworking.send(player, ProfileSyncPayload.from(
                PlayerDataManager.get(server, player.getUUID()), true));
    }
}
