package com.habilitesrandom.client.net;

import com.habilitesrandom.network.ActionRequestPayload;
import com.habilitesrandom.network.ProfileRequestPayload;
import com.habilitesrandom.network.ProfileSyncPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/** Client protocol boundary; game screens only read {@link ClientProfileCache}. */
public final class ClientNetworking {
    private ClientNetworking() {
    }

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(ProfileSyncPayload.TYPE,
                (payload, context) -> ClientProfileCache.accept(payload));
    }

    public static boolean serverHasMod() {
        return ClientPlayNetworking.canSend(ProfileRequestPayload.TYPE)
                || ClientPlayNetworking.canSend(ActionRequestPayload.TYPE);
    }

    public static void requestProfile() {
        if (ClientPlayNetworking.canSend(ProfileRequestPayload.TYPE)) {
            ClientPlayNetworking.send(new ProfileRequestPayload());
        }
    }
}
