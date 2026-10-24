package com.habilitesrandom.ability;
import com.habilitesrandom.data.PlayerProfile;
/** Stateless server cooldown authority; epoch deadlines survive reconnects and restarts. */
public final class CooldownManager {
 private CooldownManager(){}
 public static boolean ready(PlayerProfile p,int slot,long now){return deadline(p.cooldowns(),slot)<=now;}
 public static long remainingMillis(PlayerProfile p,int slot,long now){return Math.max(0,deadline(p.cooldowns(),slot)-now);}
 public static PlayerProfile start(PlayerProfile p,int slot,int ticks,long now){if(slot<0||slot>4||ticks<=0)throw new IllegalArgumentException("Invalid cooldown"); long d=now+ticks*50L; var c=p.cooldowns(); return p.withCooldowns(new PlayerProfile.Cooldowns(slot==0?d:c.ability1UntilEpochMillis(),slot==1?d:c.ability2UntilEpochMillis(),slot==2?d:c.ability3UntilEpochMillis(),slot==3?d:c.ability4UntilEpochMillis(),slot==4?d:c.formUntilEpochMillis()));}
 private static long deadline(PlayerProfile.Cooldowns c,int s){return switch(s){case 0->c.ability1UntilEpochMillis();case 1->c.ability2UntilEpochMillis();case 2->c.ability3UntilEpochMillis();case 3->c.ability4UntilEpochMillis();case 4->c.formUntilEpochMillis();default->Long.MAX_VALUE;};}
}
