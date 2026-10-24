package com.habilitesrandom.api;

import net.minecraft.resources.Identifier;

/** Declarative future transformation; no transformation logic runs in this stage. */
public record TransformationDefinition(
        Identifier id,
        String displayName,
        String translationKey,
        int durationTicks,
        int maxUses
) implements CatalogEntry {
    public TransformationDefinition {
        if (id == null || displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("Transformation definitions require an id and display name");
        }
        durationTicks = Math.max(0, durationTicks);
        maxUses = Math.max(0, maxUses);
        translationKey = translationKey == null ? "" : translationKey;
    }
}
