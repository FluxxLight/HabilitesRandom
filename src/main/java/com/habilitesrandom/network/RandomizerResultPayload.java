package com.habilitesrandom.network;
import com.habilitesrandom.HabilitesRandom;import net.minecraft.network.RegistryFriendlyByteBuf;import net.minecraft.network.codec.*;import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
/** Server-decided result. Client uses it only to stage the reveal animation. */
public record RandomizerResultPayload(boolean success,String reason,String characterId,String characterName,String animeName,String category,String rarityId,String rarityName,int rarityColor) implements CustomPacketPayload {
 public static final Type<RandomizerResultPayload> TYPE=new Type<>(HabilitesRandom.id("randomizer_result"));public static final StreamCodec<RegistryFriendlyByteBuf,RandomizerResultPayload> CODEC=StreamCodec.composite(ByteBufCodecs.BOOL,RandomizerResultPayload::success,ByteBufCodecs.stringUtf8(128),RandomizerResultPayload::reason,ByteBufCodecs.stringUtf8(128),RandomizerResultPayload::characterId,ByteBufCodecs.stringUtf8(128),RandomizerResultPayload::characterName,ByteBufCodecs.stringUtf8(128),RandomizerResultPayload::animeName,ByteBufCodecs.stringUtf8(128),RandomizerResultPayload::category,ByteBufCodecs.stringUtf8(128),RandomizerResultPayload::rarityId,ByteBufCodecs.stringUtf8(128),RandomizerResultPayload::rarityName,ByteBufCodecs.INT,RandomizerResultPayload::rarityColor,RandomizerResultPayload::new);
 @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
 public static RandomizerResultPayload denied(String reason){return new RandomizerResultPayload(false,reason,"","","","","","",0xFFFFFFFF);}
}
