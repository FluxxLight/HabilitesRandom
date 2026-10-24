package com.habilitesrandom.api;

import net.minecraft.resources.Identifier;

/** Declarative anime/source metadata registered by future Plus mods. */
public record AnimeDefinition(Identifier id, String displayName, String translationKey) implements CatalogEntry {
    public AnimeDefinition {
        if (id == null || displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("Anime definitions require an id and display name");
        }
        translationKey = translationKey == null ? "" : translationKey;
    }
}
