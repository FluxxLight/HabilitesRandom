package com.habilitesrandom.data;

import com.habilitesrandom.network.NetworkSync;
import com.habilitesrandom.player.ServerActionGateway;
import net.minecraft.server.level.ServerPlayer;

/** Join/disconnect lifecycle. No client-provided profile is ever accepted. */
public final class PlayerProfileHooks {
    private PlayerProfileHooks() {
    }

    public static void onJoin(ServerPlayer player) {
        PlayerProfile profile = PlayerDataManager.initialize(player);
        if (ServerRuntime.server() != null) {
            // Touch the stored copy once per session so updated_at remains useful for diagnostics.
            PlayerDataManager.update(ServerRuntime.server(), player.getUUID(), profile);
        }
        NetworkSync.sendProfile(player);
    }

    public static void onDisconnect(ServerPlayer player) {
        ServerActionGateway.forget(player.getUUID());
        PlayerDataManager.flush(ServerRuntime.server());
    }
}
