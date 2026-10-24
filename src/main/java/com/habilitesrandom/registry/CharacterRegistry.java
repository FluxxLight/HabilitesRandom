package com.habilitesrandom.registry;
import com.habilitesrandom.api.*; import net.minecraft.resources.Identifier; import java.util.*;
/** Query façade used by the future randomizer; weights remain raw and independently configurable. */
public record CharacterRegistry(ContentCatalog catalog) {
 public Optional<CharacterDefinition> get(Identifier id){return catalog.find(ContentTypes.CHARACTER,id);} public boolean contains(Identifier id){return get(id).isPresent();}
 public List<CharacterDefinition> getAll(){return catalog.snapshot(ContentTypes.CHARACTER);}
 public List<CharacterDefinition> getByAnime(Identifier id){return getAll().stream().filter(c->c.animeId().equals(id)).toList();}
 public List<CharacterDefinition> getByCategory(StrengthCategory c){return getAll().stream().filter(x->x.category()==c).toList();}
 public List<CharacterDefinition> getByRarity(Identifier id){return getAll().stream().filter(c->c.rarityId().equals(id)).toList();}
 public long totalWeight(){return getAll().stream().mapToLong(CharacterDefinition::drawWeight).sum();}
}
