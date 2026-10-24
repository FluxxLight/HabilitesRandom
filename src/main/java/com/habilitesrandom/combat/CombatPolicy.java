package com.habilitesrandom.combat;
/** Server configurable safety limits; immutable defaults prevent unbounded world edits. */
public record CombatPolicy(boolean playerDamage, boolean blockDestruction, int maxTargets, int maxBlocks, double maxRange) {
 public static final CombatPolicy SAFE_DEFAULTS=new CombatPolicy(true,false,64,128,64);
 public CombatPolicy {maxTargets=Math.max(1,Math.min(maxTargets,256));maxBlocks=Math.max(0,Math.min(maxBlocks,512));maxRange=Math.max(1,Math.min(maxRange,128));}
}
