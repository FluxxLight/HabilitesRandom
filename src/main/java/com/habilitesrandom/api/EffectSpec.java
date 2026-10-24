package com.habilitesrandom.api;

/** Namespaced, extensible status effect request. */
public record EffectSpec(String effectId, int durationTicks, int amplifier, double chance) {
    public EffectSpec {
        if (effectId == null || effectId.isBlank() || durationTicks < 0 || amplifier < 0 || chance < 0 || chance > 1)
            throw new IllegalArgumentException("Invalid effect specification");
    }
}
