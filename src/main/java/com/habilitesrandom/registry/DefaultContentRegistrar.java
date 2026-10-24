package com.habilitesrandom.registry;

import com.habilitesrandom.api.AbilityDefinition;
import com.habilitesrandom.api.AnimeDefinition;
import com.habilitesrandom.api.CatalogEntry;
import com.habilitesrandom.api.CharacterDefinition;
import com.habilitesrandom.api.ContentKind;
import com.habilitesrandom.api.ContentTypes;
import com.habilitesrandom.api.GenericContentDefinition;
import com.habilitesrandom.api.HabilitesRandomRegistrar;
import com.habilitesrandom.api.RarityDefinition;
import com.habilitesrandom.api.TransformationDefinition;

/** Validating façade handed to one addon; its id is attached to every diagnostic. */
public record DefaultContentRegistrar(ContentCatalog catalog, String sourceAddon, int apiVersion)
        implements HabilitesRandomRegistrar {

    public DefaultContentRegistrar {
        if (catalog == null || sourceAddon == null || sourceAddon.isBlank()) {
            throw new IllegalArgumentException("Registrar requires a catalog and source addon id");
        }
    }

    @Override
    public boolean registerAnime(AnimeDefinition definition) {
        return register(ContentTypes.ANIME, definition);
    }

    @Override
    public boolean registerRarity(RarityDefinition definition) {
        return register(ContentTypes.RARITY, definition);
    }

    @Override
    public boolean registerCharacter(CharacterDefinition definition) {
        return register(ContentTypes.CHARACTER, definition);
    }

    @Override
    public boolean registerAbility(AbilityDefinition definition) {
        return register(ContentTypes.ABILITY, definition);
    }

    @Override
    public boolean registerTransformation(TransformationDefinition definition) {
        return register(ContentTypes.TRANSFORMATION, definition);
    }

    @Override
    public boolean registerGeneric(GenericContentDefinition definition) {
        return register(ContentTypes.GENERIC, definition);
    }

    @Override
    public <T extends CatalogEntry> boolean register(ContentKind<T> kind, T definition) {
        return catalog.register(kind, definition, sourceAddon);
    }
}
