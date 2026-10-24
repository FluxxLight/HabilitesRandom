package com.habilitesrandom.network;

import com.habilitesrandom.HabilitesRandom;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** Forward-compatible action request. Prompt 3+ will attach server handlers to validated actions. */
public record ActionRequestPayload(String action, int slot) implements CustomPacketPayload {
    private static final int ACTION_LIMIT = 32;

    public static final Type<ActionRequestPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(HabilitesRandom.MOD_ID, "action_request"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ActionRequestPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.stringUtf8(ACTION_LIMIT), ActionRequestPayload::action,
            ByteBufCodecs.VAR_INT, ActionRequestPayload::slot,
            ActionRequestPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
