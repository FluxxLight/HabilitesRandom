package com.habilitesrandom.data;

import com.habilitesrandom.api.StrengthCategory;
import com.habilitesrandom.util.ProfileValues;
import com.mojang.serialization.Codec;

import java.util.List;

/**
 * Immutable server-side player state. Fields for upcoming systems exist now, but only the server may
 * change them. Optional/defaulted serialization makes later additions backward-compatible.
 */
public record PlayerProfile(
        int schema,
        Identity identity,
        Progress progress,
        Loadout loadout,
        Cooldowns cooldowns,
        long createdAtEpochMillis,
        long updatedAtEpochMillis
) {
    public static final int SCHEMA_VERSION = 1;
    public static final Codec<PlayerProfile> CODEC = PlayerProfileCodecs.CODEC;

    public PlayerProfile {
        schema = schema <= 0 ? SCHEMA_VERSION : schema;
        identity = identity == null ? Identity.empty() : identity;
        progress = progress == null ? Progress.empty() : progress;
        loadout = loadout == null ? Loadout.empty() : loadout;
        cooldowns = cooldowns == null ? Cooldowns.empty() : cooldowns;
        createdAtEpochMillis = createdAtEpochMillis > 0 ? createdAtEpochMillis : System.currentTimeMillis();
        updatedAtEpochMillis = updatedAtEpochMillis > 0 ? updatedAtEpochMillis : createdAtEpochMillis;
    }

    public static PlayerProfile empty() {
        return empty(System.currentTimeMillis());
    }

    public static PlayerProfile empty(long now) {
        return new PlayerProfile(SCHEMA_VERSION, Identity.empty(), Progress.empty(), Loadout.empty(),
                Cooldowns.empty(), now, now);
    }

    /** Codec constructor. Values from an old or corrupt file are normalized before use. */
    public static PlayerProfile create(int schema, Identity identity, Progress progress, Loadout loadout,
                                       Cooldowns cooldowns, long createdAt, long updatedAt) {
        return new PlayerProfile(Math.min(schema <= 0 ? SCHEMA_VERSION : schema, SCHEMA_VERSION),
                identity, progress, loadout, cooldowns, createdAt, updatedAt);
    }

    public PlayerProfile touch(long now) {
        return new PlayerProfile(schema, identity, progress, loadout, cooldowns, createdAtEpochMillis, now);
    }

    public PlayerProfile withIdentity(Identity newIdentity) {
        return new PlayerProfile(schema, newIdentity, progress, loadout, cooldowns, createdAtEpochMillis,
                System.currentTimeMillis());
    }

    public PlayerProfile withProgress(Progress newProgress) {
        return new PlayerProfile(schema, identity, newProgress, loadout, cooldowns, createdAtEpochMillis,
                System.currentTimeMillis());
    }

    public PlayerProfile withLoadout(Loadout newLoadout) {
        return new PlayerProfile(schema, identity, progress, newLoadout, cooldowns, createdAtEpochMillis,
                System.currentTimeMillis());
    }

    public PlayerProfile withCooldowns(Cooldowns newCooldowns) {
        return new PlayerProfile(schema, identity, progress, loadout, newCooldowns, createdAtEpochMillis,
                System.currentTimeMillis());
    }

    public boolean hasCharacter() {
        return !identity.characterId().isBlank();
    }

    public record Identity(
            String characterId,
            String animeId,
            String rarityId,
            String category,
            String categoryLabel
    ) {
        public Identity {
            characterId = ProfileValues.cleanId(characterId);
            animeId = ProfileValues.cleanId(animeId);
            rarityId = ProfileValues.cleanId(rarityId);
            category = StrengthCategory.fromString(category).serializedName();
            categoryLabel = ProfileValues.clean(categoryLabel);
        }

        public static Identity empty() {
            return new Identity("", "", "", StrengthCategory.UNKNOWN.serializedName(), "");
        }

        public StrengthCategory strengthCategory() {
            return StrengthCategory.fromString(category);
        }
    }

    public record Progress(
            int rerollsRemaining,
            boolean rerollUsed,
            int sanctuaryProgress,
            boolean sanctuaryDiscovered,
            String sanctuaryDimension,
            int sanctuaryX,
            int sanctuaryY,
            int sanctuaryZ
    ) {
        public Progress {
            rerollsRemaining = ProfileValues.nonNegative(rerollsRemaining);
            sanctuaryProgress = ProfileValues.clamp(sanctuaryProgress, 0, 100);
            sanctuaryDimension = ProfileValues.cleanId(sanctuaryDimension);
        }

        public static Progress empty() {
            return new Progress(0, false, 0, false, "", 0, 0, 0);
        }

        public RerollState rerollState() {
            if (rerollUsed && rerollsRemaining <= 0) {
                return RerollState.SANCTUARY_REQUIRED;
            }
            return rerollsRemaining > 0 ? RerollState.AVAILABLE : RerollState.UNINITIALIZED;
        }
    }

    public record Loadout(
            List<String> abilityIds,
            String transformationId,
            int transformationUses,
            boolean powerfulFruitActive,
            int powerfulFruitUses,
            int badLuckCounter
    ) {
        public Loadout {
            abilityIds = abilityIds == null ? List.of() : List.copyOf(abilityIds.stream()
                    .map(ProfileValues::cleanId)
                    .filter(value -> !value.isBlank())
                    .limit(ProfileValues.MAX_ABILITIES)
                    .toList());
            transformationId = ProfileValues.cleanId(transformationId);
            transformationUses = ProfileValues.nonNegative(transformationUses);
            powerfulFruitUses = ProfileValues.nonNegative(powerfulFruitUses);
            badLuckCounter = ProfileValues.nonNegative(badLuckCounter);
        }

        public static Loadout empty() {
            return new Loadout(List.of(), "", 0, false, 0, 0);
        }
    }

    public record Cooldowns(
            long ability1UntilEpochMillis,
            long ability2UntilEpochMillis,
            long ability3UntilEpochMillis,
            long ability4UntilEpochMillis,
            long formUntilEpochMillis
    ) {
        public Cooldowns {
            ability1UntilEpochMillis = Math.max(0, ability1UntilEpochMillis);
            ability2UntilEpochMillis = Math.max(0, ability2UntilEpochMillis);
            ability3UntilEpochMillis = Math.max(0, ability3UntilEpochMillis);
            ability4UntilEpochMillis = Math.max(0, ability4UntilEpochMillis);
            formUntilEpochMillis = Math.max(0, formUntilEpochMillis);
        }

        public static Cooldowns empty() {
            return new Cooldowns(0, 0, 0, 0, 0);
        }

        public int[] remainingSeconds(long now) {
            return new int[]{
                    seconds(ability1UntilEpochMillis, now),
                    seconds(ability2UntilEpochMillis, now),
                    seconds(ability3UntilEpochMillis, now),
                    seconds(ability4UntilEpochMillis, now)
            };
        }

        public int formRemainingSeconds(long now) {
            return seconds(formUntilEpochMillis, now);
        }

        private static int seconds(long deadline, long now) {
            if (deadline <= now) {
                return 0;
            }
            return (int) Math.min(Integer.MAX_VALUE, Math.ceil((deadline - now) / 1000.0));
        }
    }

    public enum RerollState {
        UNINITIALIZED,
        AVAILABLE,
        SANCTUARY_REQUIRED
    }
}
