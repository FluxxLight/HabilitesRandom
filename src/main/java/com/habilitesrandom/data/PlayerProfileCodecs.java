package com.habilitesrandom.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

/** Explicit codecs with defaults for every field, preserving compatibility across future prompts. */
public final class PlayerProfileCodecs {
    private static final Codec<PlayerProfile.Identity> IDENTITY = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("character_id", "").forGetter(PlayerProfile.Identity::characterId),
            Codec.STRING.optionalFieldOf("anime_id", "").forGetter(PlayerProfile.Identity::animeId),
            Codec.STRING.optionalFieldOf("rarity_id", "").forGetter(PlayerProfile.Identity::rarityId),
            Codec.STRING.optionalFieldOf("category", "unknown").forGetter(PlayerProfile.Identity::category),
            Codec.STRING.optionalFieldOf("category_label", "").forGetter(PlayerProfile.Identity::categoryLabel)
    ).apply(instance, PlayerProfile.Identity::new));

    private static final Codec<PlayerProfile.Progress> PROGRESS = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("rerolls_remaining", 0).forGetter(PlayerProfile.Progress::rerollsRemaining),
            Codec.BOOL.optionalFieldOf("reroll_used", false).forGetter(PlayerProfile.Progress::rerollUsed),
            Codec.INT.optionalFieldOf("sanctuary_progress", 0).forGetter(PlayerProfile.Progress::sanctuaryProgress),
            Codec.BOOL.optionalFieldOf("sanctuary_discovered", false).forGetter(PlayerProfile.Progress::sanctuaryDiscovered),
            Codec.STRING.optionalFieldOf("sanctuary_dimension", "").forGetter(PlayerProfile.Progress::sanctuaryDimension),
            Codec.INT.optionalFieldOf("sanctuary_x", 0).forGetter(PlayerProfile.Progress::sanctuaryX),
            Codec.INT.optionalFieldOf("sanctuary_y", 0).forGetter(PlayerProfile.Progress::sanctuaryY),
            Codec.INT.optionalFieldOf("sanctuary_z", 0).forGetter(PlayerProfile.Progress::sanctuaryZ)
    ).apply(instance, PlayerProfile.Progress::new));

    private static final Codec<PlayerProfile.Loadout> LOADOUT = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.listOf().optionalFieldOf("ability_ids", List.of()).forGetter(PlayerProfile.Loadout::abilityIds),
            Codec.STRING.optionalFieldOf("transformation_id", "").forGetter(PlayerProfile.Loadout::transformationId),
            Codec.INT.optionalFieldOf("transformation_uses", 0).forGetter(PlayerProfile.Loadout::transformationUses),
            Codec.BOOL.optionalFieldOf("powerful_fruit_active", false).forGetter(PlayerProfile.Loadout::powerfulFruitActive),
            Codec.INT.optionalFieldOf("powerful_fruit_uses", 0).forGetter(PlayerProfile.Loadout::powerfulFruitUses),
            Codec.INT.optionalFieldOf("bad_luck_counter", 0).forGetter(PlayerProfile.Loadout::badLuckCounter)
    ).apply(instance, PlayerProfile.Loadout::new));

    private static final Codec<PlayerProfile.Cooldowns> COOLDOWNS = RecordCodecBuilder.create(instance -> instance.group(
            Codec.LONG.optionalFieldOf("ability_1_until", 0L).forGetter(PlayerProfile.Cooldowns::ability1UntilEpochMillis),
            Codec.LONG.optionalFieldOf("ability_2_until", 0L).forGetter(PlayerProfile.Cooldowns::ability2UntilEpochMillis),
            Codec.LONG.optionalFieldOf("ability_3_until", 0L).forGetter(PlayerProfile.Cooldowns::ability3UntilEpochMillis),
            Codec.LONG.optionalFieldOf("ability_4_until", 0L).forGetter(PlayerProfile.Cooldowns::ability4UntilEpochMillis),
            Codec.LONG.optionalFieldOf("form_until", 0L).forGetter(PlayerProfile.Cooldowns::formUntilEpochMillis)
    ).apply(instance, PlayerProfile.Cooldowns::new));

    public static final Codec<PlayerProfile> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("schema", PlayerProfile.SCHEMA_VERSION).forGetter(PlayerProfile::schema),
            IDENTITY.optionalFieldOf("identity", PlayerProfile.Identity.empty()).forGetter(PlayerProfile::identity),
            PROGRESS.optionalFieldOf("progress", PlayerProfile.Progress.empty()).forGetter(PlayerProfile::progress),
            LOADOUT.optionalFieldOf("loadout", PlayerProfile.Loadout.empty()).forGetter(PlayerProfile::loadout),
            COOLDOWNS.optionalFieldOf("cooldowns", PlayerProfile.Cooldowns.empty()).forGetter(PlayerProfile::cooldowns),
            Codec.LONG.optionalFieldOf("created_at", 0L).forGetter(PlayerProfile::createdAtEpochMillis),
            Codec.LONG.optionalFieldOf("updated_at", 0L).forGetter(PlayerProfile::updatedAtEpochMillis)
    ).apply(instance, PlayerProfile::create));

    private PlayerProfileCodecs() {
    }
}
