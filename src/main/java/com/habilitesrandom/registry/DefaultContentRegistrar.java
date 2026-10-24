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
import com.habilitesrandom.api.execution.*;
import com.habilitesrandom.core.CoreServices;
import net.minecraft.resources.Identifier;

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
        if (definition == null || definition.abilityIds().stream().anyMatch(id -> catalog.find(ContentTypes.ABILITY,id).isEmpty()) || catalog.find(ContentTypes.TRANSFORMATION,definition.transformationId()).isEmpty() || catalog.find(ContentTypes.ANIME,definition.animeId()).isEmpty() || catalog.find(ContentTypes.RARITY,definition.rarityId()).isEmpty()) return false;
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

    @Override public boolean registerAbilityExecutor(Identifier id, AbilityExecutor executor) { return CoreServices.executions().registerAbility(id, executor); }
    @Override public boolean registerTransformationHooks(Identifier id, TransformationHooks hooks) { return CoreServices.executions().registerTransformation(id, hooks); }

    @Override
    public boolean registerGeneric(GenericContentDefinition definition) {
        return register(ContentTypes.GENERIC, definition);
    }

    @Override
    public <T extends CatalogEntry> boolean register(ContentKind<T> kind, T definition) {
        return catalog.register(kind, definition, sourceAddon);
    }
}
