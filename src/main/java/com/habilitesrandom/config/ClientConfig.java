package com.habilitesrandom.config;

import java.util.Locale;

/** Versioned client preferences. Gameplay values are intentionally absent. */
public final class ClientConfig {
    public static final int CURRENT_VERSION = 1;

    public enum EffectIntensity {
        LOW,
        MEDIUM,
        HIGH;

        public static EffectIntensity from(String value) {
            if (value != null) {
                for (EffectIntensity level : values()) {
                    if (level.name().equalsIgnoreCase(value)) {
                        return level;
                    }
                }
            }
            return MEDIUM;
        }

        public String translationKey() {
            return "habilitesrandom.settings.effects.level." + name().toLowerCase(Locale.ROOT);
        }
    }

    public int version = CURRENT_VERSION;

    // Interface
    public boolean interfaceAnimations = true;
    public boolean interfaceParticles = true;
    public boolean hudVisible = true;
    public boolean compactHud = false;
    public boolean hudGlow = true;
    public String hudPosition = "BOTTOM_RIGHT";
    public float hudScale = 1.0F;
    public float hudOpacity = 0.9F;
    public boolean hudShowNames = true;
    public boolean hudShowKeys = true;

    // Effects
    public boolean visualEffects = true;
    public String effectIntensity = EffectIntensity.MEDIUM.name();

    // Audio (prepared; no sound assets are shipped yet)
    public boolean interfaceSounds = true;
    public boolean ambientUiAudio = false;

    // General
    public boolean openHubOnJoin = true;
    public boolean showHubHint = true;
    public boolean advancedTooltips = true;

    public void sanitize() {
        version = CURRENT_VERSION;
        effectIntensity = EffectIntensity.from(effectIntensity).name();
        if (!java.util.Set.of("BOTTOM_LEFT", "BOTTOM_RIGHT", "BOTTOM_CENTER").contains(hudPosition)) hudPosition = "BOTTOM_RIGHT";
        hudScale = Math.max(0.65F, Math.min(1.5F, hudScale));
        hudOpacity = Math.max(0.25F, Math.min(1.0F, hudOpacity));
    }

    public EffectIntensity intensity() {
        return EffectIntensity.from(effectIntensity);
    }

    public void cycleIntensity() {
        EffectIntensity[] values = EffectIntensity.values();
        int next = (intensity().ordinal() + 1) % values.length;
        effectIntensity = values[next].name();
    }
}
