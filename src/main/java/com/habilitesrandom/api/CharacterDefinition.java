package com.habilitesrandom.api;

import net.minecraft.resources.Identifier;

import java.util.List;

/**
 * Pure content declaration. Prompt 2 will add behavior and weighted draws without changing this
 * record's role as the contract between the Core and Plus mods.
 */
public record CharacterDefinition(
        Identifier id,
        String displayName,
        Identifier animeId,
        Identifier rarityId,
        StrengthCategory category,
        List<Identifier> abilityIds,
        Identifier transformationId
) implements CatalogEntry {
    public CharacterDefinition {
        if (id == null || displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("Character definitions require an id and display name");
        }
        category = category == null ? StrengthCategory.UNKNOWN : category;
        abilityIds = abilityIds == null ? List.of() : List.copyOf(abilityIds);
    }
}
