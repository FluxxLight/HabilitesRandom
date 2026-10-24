package com.habilitesrandom.ability;
import com.habilitesrandom.api.*;import com.habilitesrandom.combat.*;import com.habilitesrandom.config.server.*;import net.minecraft.server.level.*;import net.minecraft.world.entity.*;import net.minecraft.world.phys.*;import java.util.*;
/** Bounded server executor. Projectile/beam data use a collision corridor without spawning disposable entities. */
public final class ServerAbilityExecutor {private ServerAbilityExecutor(){}
 public record Outcome(int targetsHit,float damageDealt){}
 public static Outcome execute(ServerPlayer source,AbilityDefinition ability){ServerConfig cfg=ServerConfigStore.get();CombatPolicy policy=new CombatPolicy(cfg.abilityExecutionEnabled,cfg.blockDestruction,cfg.maxTargets,cfg.maxBlocksPerAbility,64);AbilityExecutionPlan plan=AbilityExecutionPlan.create(ability,policy);if(!cfg.abilityExecutionEnabled)return new Outcome(0,0);
  ServerLevel level=source.level();Vec3 origin=source.position();Vec3 look=source.getLookAngle().normalize();double reach=Math.max(2,plan.effectiveRange());AABB bounds=source.getBoundingBox().inflate(reach);
  List<LivingEntity> candidates=level.getEntitiesOfClass(LivingEntity.class,bounds,e->e!=source&&e.isAlive());int hits=0;float dealt=0;
  for(LivingEntity target:candidates){if(hits>=plan.targetLimit())break;if(target instanceof ServerPlayer&&!ability.targetsPlayers())continue;if(!(target instanceof ServerPlayer)&&!ability.targetsEntities())continue;Vec3 p=target.position();boolean inside=ability.areaShape()==AreaShape.NONE?origin.distanceToSqr(p)<=reach*reach:CombatGeometry.contains(ability.areaShape(),origin.x,origin.y,origin.z,look.x,look.z,p.x,p.y,p.z,reach,Math.max(ability.area(),1));if(!inside)continue;
   float amount=(float)ability.damage();if(amount>0&&target.hurtServer(level,source.damageSources().playerAttack(source),amount)){hits++;dealt+=amount;Vec3 direction=target.position().subtract(origin);double length=Math.max(.001,Math.sqrt(direction.x*direction.x+direction.z*direction.z));target.push(direction.x/length*ability.knockbackHorizontal(),ability.knockbackVertical(),direction.z/length*ability.knockbackHorizontal());}}
  return new Outcome(hits,dealt);
 }
}
