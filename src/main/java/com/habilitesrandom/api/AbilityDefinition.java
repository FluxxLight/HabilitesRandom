package com.habilitesrandom.api;

import net.minecraft.resources.Identifier;
import java.util.List;
import java.util.Set;

/** Immutable server-readable ability data; execution is selected by behaviour types, never client code. */
public record AbilityDefinition(
        Identifier id, String displayName, String description, int cooldownTicks,
        double damage, double range, double area, int durationTicks,
        Set<AbilityType> types, AreaShape areaShape, List<EffectSpec> effects,
        String particleId, String soundId, BlockInteraction blockInteraction,
        double knockbackHorizontal, double knockbackVertical,
        boolean targetsEntities, boolean targetsPlayers,
        ProjectileSpec projectile, DestructionSpec destruction
) implements CatalogEntry {
    public static AbilityBuilder builder(Identifier id) { return new AbilityBuilder(id); }

    public AbilityDefinition {
        if (id == null || displayName == null || displayName.isBlank() || cooldownTicks <= 0)
            throw new IllegalArgumentException("Ability requires id, name and positive cooldown");
        description = description == null ? "" : description;
        damage = bounded(damage, 0, 1000); range = bounded(range, 0, 128); area = bounded(area, 0, 64);
        durationTicks = Math.max(0, Math.min(durationTicks, 20 * 60 * 10));
        types = types == null || types.isEmpty() ? Set.of(AbilityType.SPECIAL) : Set.copyOf(types);
        areaShape = areaShape == null ? AreaShape.NONE : areaShape;
        effects = effects == null ? List.of() : List.copyOf(effects);
        particleId = particleId == null ? "" : particleId;
        soundId = soundId == null ? "" : soundId;
        blockInteraction = blockInteraction == null ? BlockInteraction.NONE : blockInteraction;
        knockbackHorizontal = bounded(knockbackHorizontal, 0, 4); knockbackVertical = bounded(knockbackVertical, 0, 2);
        projectile = projectile == null ? ProjectileSpec.NONE : projectile;
        destruction = destruction == null ? DestructionSpec.NONE : destruction;
        if (blockInteraction == BlockInteraction.NONE) destruction = DestructionSpec.NONE;
    }
    private static double bounded(double v, double min, double max) {
        if (!Double.isFinite(v)) throw new IllegalArgumentException("Non-finite balance value");
        return Math.max(min, Math.min(max, v));
    }
    public record ProjectileSpec(double speed, double maxRange, int lifetimeTicks, boolean disappearsOnImpact) {
        public static final ProjectileSpec NONE = new ProjectileSpec(0,0,0,true);
        public ProjectileSpec { speed=bounded(speed,0,8); maxRange=bounded(maxRange,0,256); lifetimeTicks=Math.max(0,Math.min(lifetimeTicks,1200)); }
    }
    public record DestructionSpec(double radius, float strength, int maxBlocks) {
        public static final DestructionSpec NONE = new DestructionSpec(0,0,0);
        public DestructionSpec { radius=bounded(radius,0,12); strength=(float)bounded(strength,0,20); maxBlocks=Math.max(0,Math.min(maxBlocks,512)); }
    }
}
