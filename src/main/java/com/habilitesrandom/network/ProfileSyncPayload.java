package com.habilitesrandom.network;

import com.habilitesrandom.HabilitesRandom;
import com.habilitesrandom.api.AnimeDefinition;
import com.habilitesrandom.api.CharacterDefinition;
import com.habilitesrandom.api.ContentTypes;
import com.habilitesrandom.api.RarityDefinition;
import com.habilitesrandom.api.TransformationDefinition;
import com.habilitesrandom.core.CoreServices;
import com.habilitesrandom.data.PlayerProfile;
import com.habilitesrandom.util.ProfileValues;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Read-only display snapshot created by the server. It deliberately contains no mutable gameplay
 * fields: future actions are validated server-side instead of trusting a client-provided profile.
 */
public record ProfileSyncPayload(Core core, Progress progress) implements CustomPacketPayload {
    private static final int TEXT_LIMIT = 128;

    public static final Type<ProfileSyncPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(HabilitesRandom.MOD_ID, "profile_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ProfileSyncPayload> CODEC = StreamCodec.composite(
            Core.CODEC, ProfileSyncPayload::core,
            Progress.CODEC, ProfileSyncPayload::progress,
            ProfileSyncPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static ProfileSyncPayload empty() {
        return new ProfileSyncPayload(
                new Core(false, false, "", "", "", "", "", "unknown"),
                new Progress("UNINITIALIZED", "", 0, "0,0,0,0", 0, false, 0, "UNDISCOVERED"));
    }

    public static ProfileSyncPayload from(PlayerProfile profile, boolean present) {
        PlayerProfile safe = profile == null ? PlayerProfile.empty() : profile;
        PlayerProfile.Identity identity = safe.identity();
        long now = System.currentTimeMillis();

        CharacterDefinition character = findCharacter(identity.characterId());
        String animeId = character != null && character.animeId() != null
                ? character.animeId().toString() : identity.animeId();
        String rarityId = character != null && character.rarityId() != null
                ? character.rarityId().toString() : identity.rarityId();

        Core core = new Core(
                present,
                safe.hasCharacter(),
                ProfileValues.clean(identity.characterId()),
                character != null ? ProfileValues.clean(character.displayName()) : "",
                animeName(animeId),
                ProfileValues.clean(rarityId),
                rarityName(rarityId),
                identity.strengthCategory().serializedName());

        Progress progress = new Progress(
                safe.progress().rerollState().name(),
                transformationName(safe.loadout().transformationId()),
                safe.loadout().transformationUses(),
                ProfileValues.joinCooldowns(safe.cooldowns().remainingSeconds(now)),
                safe.loadout().badLuckCounter(),
                safe.loadout().powerfulFruitActive(),
                safe.progress().sanctuaryProgress(),
                safe.progress().sanctuaryDiscovered() ? "DISCOVERED" : "UNDISCOVERED");

        return new ProfileSyncPayload(core, progress);
    }

    private static CharacterDefinition findCharacter(String id) {
        Identifier identifier = ProfileValues.parseId(id);
        return identifier == null ? null : CoreServices.catalog().find(ContentTypes.CHARACTER, identifier).orElse(null);
    }

    private static String animeName(String id) {
        Identifier identifier = ProfileValues.parseId(id);
        return identifier == null ? "" : CoreServices.catalog().find(ContentTypes.ANIME, identifier)
                .map(AnimeDefinition::displayName).map(ProfileValues::clean).orElse("");
    }

    private static String rarityName(String id) {
        Identifier identifier = ProfileValues.parseId(id);
        return identifier == null ? "" : CoreServices.catalog().find(ContentTypes.RARITY, identifier)
                .map(RarityDefinition::displayName).map(ProfileValues::clean).orElse("");
    }

    private static String transformationName(String id) {
        Identifier identifier = ProfileValues.parseId(id);
        return identifier == null ? "" : CoreServices.catalog().find(ContentTypes.TRANSFORMATION, identifier)
                .map(TransformationDefinition::displayName).map(ProfileValues::clean).orElse("");
    }

    public record Core(
            boolean profilePresent,
            boolean hasCharacter,
            String characterId,
            String characterName,
            String animeName,
            String rarityId,
            String rarityName,
            String category
    ) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Core> CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL, Core::profilePresent,
                ByteBufCodecs.BOOL, Core::hasCharacter,
                ByteBufCodecs.stringUtf8(TEXT_LIMIT), Core::characterId,
                ByteBufCodecs.stringUtf8(TEXT_LIMIT), Core::characterName,
                ByteBufCodecs.stringUtf8(TEXT_LIMIT), Core::animeName,
                ByteBufCodecs.stringUtf8(TEXT_LIMIT), Core::rarityId,
                ByteBufCodecs.stringUtf8(TEXT_LIMIT), Core::rarityName,
                ByteBufCodecs.stringUtf8(TEXT_LIMIT), Core::category,
                Core::new);
    }

    public record Progress(
            String rerollState,
            String transformationName,
            int transformationUses,
            String cooldownSeconds,
            int badLuckCounter,
            boolean powerfulFruitActive,
            int sanctuaryProgress,
            String sanctuaryHint
    ) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Progress> CODEC = StreamCodec.composite(
                ByteBufCodecs.stringUtf8(TEXT_LIMIT), Progress::rerollState,
                ByteBufCodecs.stringUtf8(TEXT_LIMIT), Progress::transformationName,
                ByteBufCodecs.VAR_INT, Progress::transformationUses,
                ByteBufCodecs.stringUtf8(TEXT_LIMIT), Progress::cooldownSeconds,
                ByteBufCodecs.VAR_INT, Progress::badLuckCounter,
                ByteBufCodecs.BOOL, Progress::powerfulFruitActive,
                ByteBufCodecs.VAR_INT, Progress::sanctuaryProgress,
                ByteBufCodecs.stringUtf8(TEXT_LIMIT), Progress::sanctuaryHint,
                Progress::new);
    }
}
