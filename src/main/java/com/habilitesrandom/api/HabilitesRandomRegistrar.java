package com.habilitesrandom.api;

/**
 * Public registration surface for HabilitesRandom Plus. Calls are validated by the Core and record
 * the addon that owns each entry; duplicate ids are rejected instead of silently overwritten.
 */
public interface HabilitesRandomRegistrar {
    int apiVersion();

    boolean registerAnime(AnimeDefinition definition);

    boolean registerRarity(RarityDefinition definition);

    boolean registerCharacter(CharacterDefinition definition);

    boolean registerAbility(AbilityDefinition definition);

    boolean registerTransformation(TransformationDefinition definition);

    boolean registerGeneric(GenericContentDefinition definition);

    <T extends CatalogEntry> boolean register(ContentKind<T> kind, T definition);
}
