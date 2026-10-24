package com.habilitesrandom.exampleplus;
import com.habilitesrandom.api.*;import com.habilitesrandom.api.execution.*;import net.minecraft.resources.Identifier;
/** Minimal external addon: no Core implementation package is imported. */
public final class TestPlusAddon implements HabilitesRandomAddon {
 private static Identifier id(String path){return Identifier.fromNamespaceAndPath("habilitesrandom_test_plus",path);}
 @Override public String addonId(){return "habilitesrandom_test_plus";}
 @Override public int requiredApiVersion(){return 1;}
 @Override public void register(HabilitesRandomRegistrar r){
  var anime=new AnimeDefinition(id("test_universe"),"Test Universe","anime.habilitesrandom_test_plus.test_universe");r.registerAnime(anime);
  Identifier[] ids={id("pulse_strike"),id("arc_shot"),id("gravity_ring"),id("phase_step")};
  r.registerAbility(AbilityDefinition.builder(ids[0]).displayName("Pulse Strike").description("A server-executed test strike.").cooldownTicks(120).damage(7).range(5).types(AbilityType.MELEE).presentation("minecraft:crit","minecraft:entity.player.attack.strong").build());
  r.registerAbility(AbilityDefinition.builder(ids[1]).displayName("Arc Shot").description("A test projectile corridor.").cooldownTicks(180).damage(8).range(20).types(AbilityType.PROJECTILE).projectile(1.2,20,60).presentation("minecraft:electric_spark","minecraft:entity.arrow.shoot").build());
  r.registerAbility(AbilityDefinition.builder(ids[2]).displayName("Gravity Ring").description("A controlled test area.").cooldownTicks(300).damage(6).range(8).area(AreaShape.CIRCLE,6).types(AbilityType.AREA).knockback(.8,.15).presentation("minecraft:portal","minecraft:block.amethyst_block.chime").build());
  r.registerAbility(AbilityDefinition.builder(ids[3]).displayName("Phase Step").description("A custom Plus movement executor.").cooldownTicks(240).damage(1).range(3).types(AbilityType.MOVEMENT).presentation("minecraft:reverse_portal","minecraft:entity.enderman.teleport").build());
  var form=TransformationDefinition.builder(id("overdrive")).displayName("Test Overdrive").description("Validates Core daily form integration.").durationTicks(400).cooldownTicks(1200).modifiers(1.4,1.2,1.15).presentation("minecraft:flash","minecraft:item.totem.use").build();r.registerTransformation(form);
  r.registerAbilityExecutor(ids[3],ctx->{var look=ctx.player().getLookAngle();ctx.player().push(look.x*1.4,.18,look.z*1.4);return AbilityExecutionResult.success(0,0);});
  r.registerTransformationHooks(form.id(),new TransformationHooks(){});
  r.registerCharacter(CharacterDefinition.builder(id("test_character")).displayName("Test Character").anime(anime.id()).rarity(Identifier.fromNamespaceAndPath("habilitesrandom","rare")).category(StrengthCategory.STRONG).weight(25).description("External API integration character.").abilities(ids[0],ids[1],ids[2],ids[3]).powerfulForm(form.id()).presentation("","test").build());
 }
}
