package com.habilitesrandom.api;

import com.habilitesrandom.HabilitesRandom;

/** Built-in kinds. A Plus never needs to edit this class to add its own {@link ContentKind}. */
public final class ContentTypes {
    public static final ContentKind<AnimeDefinition> ANIME =
            new ContentKind<>(HabilitesRandom.id("anime"), AnimeDefinition.class);
    public static final ContentKind<RarityDefinition> RARITY =
            new ContentKind<>(HabilitesRandom.id("rarity"), RarityDefinition.class);
    public static final ContentKind<CharacterDefinition> CHARACTER =
            new ContentKind<>(HabilitesRandom.id("character"), CharacterDefinition.class);
    public static final ContentKind<AbilityDefinition> ABILITY =
            new ContentKind<>(HabilitesRandom.id("ability"), AbilityDefinition.class);
    public static final ContentKind<TransformationDefinition> TRANSFORMATION =
            new ContentKind<>(HabilitesRandom.id("transformation"), TransformationDefinition.class);
    public static final ContentKind<GenericContentDefinition> GENERIC =
            new ContentKind<>(HabilitesRandom.id("generic"), GenericContentDefinition.class);

    private ContentTypes() {
    }
}
