package com.habilitesrandom.content;
import com.habilitesrandom.api.*; import com.habilitesrandom.core.CoreServices; import com.habilitesrandom.data.PlayerProfile; import net.minecraft.resources.Identifier;
/** Creates a complete server-owned loadout from one catalog character. */
public final class CharacterAssignmentService {
 private CharacterAssignmentService(){}
 public static PlayerProfile assign(PlayerProfile profile, Identifier id){
  CharacterDefinition c=CoreServices.characters().get(id).orElseThrow(()->new IllegalArgumentException("Unknown character: "+id));
  for(Identifier a:c.abilityIds()) if(!CoreServices.abilities().contains(a)) throw new IllegalStateException("Missing ability "+a);
  if(!CoreServices.transformations().contains(c.transformationId())) throw new IllegalStateException("Missing form "+c.transformationId());
  var identity=new PlayerProfile.Identity(c.id().toString(),c.animeId().toString(),c.rarityId().toString(),c.category().serializedName(),c.category().name());
  var loadout=new PlayerProfile.Loadout(c.abilityIds().stream().map(Identifier::toString).toList(),c.transformationId().toString(),profile.loadout().transformationUses(),profile.loadout().powerfulFruitActive(),profile.loadout().powerfulFruitUses(),profile.loadout().badLuckCounter(),profile.loadout().lastPowerfulFormDay(),0);
  return profile.withIdentity(identity).withLoadout(loadout).withCooldowns(PlayerProfile.Cooldowns.empty());
 }
}
