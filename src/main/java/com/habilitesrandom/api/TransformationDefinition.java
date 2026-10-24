package com.habilitesrandom.api;

import net.minecraft.resources.Identifier;
import java.util.List;

/** One timed powerful form consumed through Core-owned daily-use policy. */
public record TransformationDefinition(Identifier id, String displayName, String description, int durationTicks,
        int cooldownTicks, double damageMultiplier, double speedMultiplier, double defenseMultiplier,
        List<EffectSpec> effects, String particleId, String soundId) implements CatalogEntry {
    public static TransformationBuilder builder(Identifier id) { return new TransformationBuilder(id); }

    public TransformationDefinition {
        if(id==null||displayName==null||displayName.isBlank()||durationTicks<=0||cooldownTicks<=0) throw new IllegalArgumentException("Invalid transformation");
        description=description==null?"":description; damageMultiplier=bound(damageMultiplier); speedMultiplier=bound(speedMultiplier); defenseMultiplier=bound(defenseMultiplier);
        effects=effects==null?List.of():List.copyOf(effects); particleId=particleId==null?"":particleId; soundId=soundId==null?"":soundId;
    }
    private static double bound(double value){ if(!Double.isFinite(value)) throw new IllegalArgumentException("Invalid modifier"); return Math.max(.1,Math.min(10,value)); }
}
