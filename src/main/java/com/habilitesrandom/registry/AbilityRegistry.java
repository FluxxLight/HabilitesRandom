package com.habilitesrandom.registry;
import com.habilitesrandom.api.*; import net.minecraft.resources.Identifier; import java.util.*;
public record AbilityRegistry(ContentCatalog catalog) { public Optional<AbilityDefinition> get(Identifier id){return catalog.find(ContentTypes.ABILITY,id);} public boolean contains(Identifier id){return get(id).isPresent();} public List<AbilityDefinition> getAll(){return catalog.snapshot(ContentTypes.ABILITY);} }
