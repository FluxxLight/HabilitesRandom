package com.habilitesrandom.api;

import net.minecraft.resources.Identifier;
import java.util.List;

/** Complete immutable character declaration shared by Core and Plus addons. */
public record CharacterDefinition(Identifier id, String displayName, Identifier animeId, Identifier rarityId,
        StrengthCategory category, int drawWeight, String description, List<Identifier> abilityIds,
        Identifier transformationId, String iconId, String presentationTheme) implements CatalogEntry {
    public static CharacterBuilder builder(Identifier id) { return new CharacterBuilder(id); }

    public CharacterDefinition {
        if (id == null || displayName == null || displayName.isBlank() || animeId == null || rarityId == null)
            throw new IllegalArgumentException("Character identity is incomplete");
        if (category == null || category == StrengthCategory.UNKNOWN) throw new IllegalArgumentException("Character category required");
        if (drawWeight <= 0) throw new IllegalArgumentException("Draw weight must be positive");
        if (abilityIds == null || abilityIds.size() != 4 || abilityIds.stream().anyMatch(java.util.Objects::isNull))
            throw new IllegalArgumentException("Every character requires exactly four abilities");
        abilityIds = List.copyOf(abilityIds);
        if (transformationId == null) throw new IllegalArgumentException("Transformation required");
        description = description == null ? "" : description; iconId=iconId==null?"":iconId; presentationTheme=presentationTheme==null?"":presentationTheme;
    }
}
