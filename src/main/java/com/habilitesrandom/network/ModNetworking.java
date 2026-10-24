package com.habilitesrandom.network;

import com.habilitesrandom.player.ServerActionGateway;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

/** All payload registrations live here so protocol ownership is obvious. */
public final class ModNetworking {
    private ModNetworking() {
    }

    public static void register() {
        PayloadTypeRegistry.serverboundPlay().register(ProfileRequestPayload.TYPE, ProfileRequestPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ActionRequestPayload.TYPE, ActionRequestPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ProfileSyncPayload.TYPE, ProfileSyncPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ProfileRequestPayload.TYPE,
                (payload, context) -> NetworkSync.sendProfile(context.player()));
        ServerPlayNetworking.registerGlobalReceiver(ActionRequestPayload.TYPE,
                (payload, context) -> ServerActionGateway.handle(payload, context.player()));
    }
}
