package com.habilitesrandom.network;

import com.habilitesrandom.HabilitesRandom;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** A client may request a snapshot, but never supplies the snapshot itself. */
public record ProfileRequestPayload() implements CustomPacketPayload {
    public static final Type<ProfileRequestPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(HabilitesRandom.MOD_ID, "profile_request"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ProfileRequestPayload> CODEC =
            StreamCodec.unit(new ProfileRequestPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
