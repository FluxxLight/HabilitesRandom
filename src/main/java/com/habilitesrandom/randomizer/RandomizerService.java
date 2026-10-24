package com.habilitesrandom.randomizer;
import com.habilitesrandom.api.*;import com.habilitesrandom.config.server.*;import com.habilitesrandom.core.CoreServices;import com.habilitesrandom.content.CharacterAssignmentService;import com.habilitesrandom.data.PlayerProfile;import java.util.*;import java.util.random.RandomGenerator;
/** Pure server selection transaction: filter registry, weight, anti-luck, assign, update streak. */
public final class RandomizerService {private RandomizerService(){}
 public record Result(CharacterDefinition character,PlayerProfile profile,double normalizedChance){}
 public static Result draw(PlayerProfile profile,RandomGenerator random){if(profile.hasCharacter())throw new IllegalStateException("already_has_character");return select(profile,random);}
 public static Result reroll(PlayerProfile profile,RandomGenerator random){if(!profile.hasCharacter())throw new IllegalStateException("no_character");return select(profile,random);}
 private static Result select(PlayerProfile profile,RandomGenerator random){ServerConfig cfg=ServerConfigStore.get();if(!cfg.randomizerEnabled)throw new IllegalStateException("randomizer_disabled");
  List<CharacterDefinition> pool=CoreServices.characters().getAll().stream().filter(c->allowed(c,cfg)).toList();if(pool.isEmpty())throw new IllegalStateException("empty_randomizer_pool");
  var weight=(java.util.function.ToDoubleFunction<CharacterDefinition>)c->cfg.characterWeightOverrides.getOrDefault(c.id().toString(),(double)c.drawWeight())*AntiLuckPolicy.multiplier(c,profile.loadout().badLuckCounter(),cfg.antiLuckEnabled);
  CharacterDefinition selected=WeightedRandomSelector.select(pool,weight,random);double chance=WeightedRandomSelector.chance(selected,pool,weight);PlayerProfile assigned=CharacterAssignmentService.assign(profile,selected.id());var l=assigned.loadout();assigned=assigned.withLoadout(new PlayerProfile.Loadout(l.abilityIds(),l.transformationId(),l.transformationUses(),l.powerfulFruitActive(),l.powerfulFruitUses(),AntiLuckPolicy.nextStreak(profile.loadout().badLuckCounter(),selected)));return new Result(selected,assigned,chance);
 }
 private static boolean allowed(CharacterDefinition c,ServerConfig s){return !s.blockedCharacters.contains(c.id().toString())&&(s.enabledRarities.isEmpty()||s.enabledRarities.contains(c.rarityId().toString()))&&(s.enabledCategories.isEmpty()||s.enabledCategories.contains(c.category().serializedName()));}
}
