package com.habilitesrandom.registry;
import com.habilitesrandom.api.*; import java.util.*;
/** Fails startup for incomplete built-in or addon character graphs instead of exposing broken kits. */
public final class CatalogValidator {private CatalogValidator(){}
 public static void validate(ContentCatalog c){var abilities=new AbilityRegistry(c);var forms=new TransformationRegistry(c);for(CharacterDefinition x:new CharacterRegistry(c).getAll()){if(x.abilityIds().size()!=4)throw new IllegalStateException(x.id()+" does not have four abilities");if(new HashSet<>(x.abilityIds()).size()!=4)throw new IllegalStateException(x.id()+" has duplicate ability ids");for(var id:x.abilityIds())if(!abilities.contains(id))throw new IllegalStateException(x.id()+" references missing "+id);if(!forms.contains(x.transformationId()))throw new IllegalStateException(x.id()+" references missing form");}}
}
