package com.habilitesrandom.api;

import net.minecraft.resources.Identifier;

/** Metadata only in Prompt 1; execution handlers are deliberately deferred to Prompt 2. */
public record AbilityDefinition(
        Identifier id,
        String displayName,
        String translationKey,
        int cooldownTicks
) implements CatalogEntry {
    public AbilityDefinition {
        if (id == null || displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("Ability definitions require an id and display name");
        }
        cooldownTicks = Math.max(0, cooldownTicks);
        translationKey = translationKey == null ? "" : translationKey;
    }
}
