package com.habilitesrandom.combat;
import com.habilitesrandom.api.*;
/** Sanitized execution plan built only from registered server data. */
public record AbilityExecutionPlan(AbilityDefinition ability,double effectiveRange,int targetLimit,int blockLimit,boolean breakBlocks){
 public static AbilityExecutionPlan create(AbilityDefinition a,CombatPolicy p){return new AbilityExecutionPlan(a,Math.min(a.range(),p.maxRange()),p.maxTargets(),Math.min(a.destruction().maxBlocks(),p.maxBlocks()),p.blockDestruction()&&a.blockInteraction()==BlockInteraction.BREAK);}
}
