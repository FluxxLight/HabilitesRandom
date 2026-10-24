package com.habilitesrandom.client.net;

import com.habilitesrandom.network.ActionRequestPayload;
import com.habilitesrandom.network.ProfileRequestPayload;
import com.habilitesrandom.network.ProfileSyncPayload;
import com.habilitesrandom.network.RandomizerResultPayload;
import com.habilitesrandom.player.PlayerAuthorityGate;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/** Client protocol boundary; game screens only read {@link ClientProfileCache}. */
public final class ClientNetworking {
    private ClientNetworking() {
    }

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(ProfileSyncPayload.TYPE,
                (payload, context) -> ClientProfileCache.accept(payload));
        ClientPlayNetworking.registerGlobalReceiver(RandomizerResultPayload.TYPE,
                (payload, context) -> ClientProfileCache.acceptRandomizerResult(payload));
    }

    public static void sendAbility(int slot) {
        if (ClientPlayNetworking.canSend(ActionRequestPayload.TYPE)) ClientPlayNetworking.send(new ActionRequestPayload(PlayerAuthorityGate.Action.ABILITY.name(), slot));
    }

    public static void sendPowerfulForm() {
        if (ClientPlayNetworking.canSend(ActionRequestPayload.TYPE)) ClientPlayNetworking.send(new ActionRequestPayload(PlayerAuthorityGate.Action.POWERFUL_FORM.name(), 4));
    }

    public static void requestRandomizer() {
        if (ClientPlayNetworking.canSend(ActionRequestPayload.TYPE)) ClientPlayNetworking.send(new ActionRequestPayload(PlayerAuthorityGate.Action.RANDOMIZE.name(), -1));
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
