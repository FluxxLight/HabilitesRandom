package com.habilitesrandom.registry;
import com.habilitesrandom.api.*; import net.minecraft.resources.Identifier; import java.util.*;
public record TransformationRegistry(ContentCatalog catalog) { public Optional<TransformationDefinition> get(Identifier id){return catalog.find(ContentTypes.TRANSFORMATION,id);} public boolean contains(Identifier id){return get(id).isPresent();} public List<TransformationDefinition> getAll(){return catalog.snapshot(ContentTypes.TRANSFORMATION);} }
