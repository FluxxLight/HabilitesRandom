package com.habilitesrandom.api.execution;

/** Lightweight result for diagnostics/events; executors must apply side effects on the logical server. */
public record AbilityExecutionResult(boolean executed, int targetsAffected, float damageDealt, String detail) {
    public AbilityExecutionResult { targetsAffected=Math.max(0,targetsAffected);damageDealt=Math.max(0,damageDealt);detail=detail==null?"":detail; }
    public static AbilityExecutionResult success(int targets,float damage){return new AbilityExecutionResult(true,targets,damage,"ok");}
    public static AbilityExecutionResult rejected(String reason){return new AbilityExecutionResult(false,0,0,reason);}
}
