package com.habilitesrandom.api;

import net.minecraft.resources.Identifier;

/** Externally registered rarity. Weight and color are data, never hard-coded in the UI. */
public record RarityDefinition(
        Identifier id,
        String displayName,
        int color,
        int weight,
        int sortOrder
) implements CatalogEntry {
    public RarityDefinition {
        if (id == null || displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("Rarity definitions require an id and display name");
        }
        weight = Math.max(0, weight);
    }
}
