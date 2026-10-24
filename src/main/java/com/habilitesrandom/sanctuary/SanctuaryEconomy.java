package com.habilitesrandom.sanctuary;
/** Pure atomic transaction calculator; adapters commit only after every precondition passes. */
public final class SanctuaryEconomy {private SanctuaryEconomy(){}
 public record Quote(int emeraldCost,float healthCost,float remainingHealth,boolean affordable,String denial){}
 public static Quote quote(int emeralds,float health,int emeraldCost,double healthFraction,float minimumHealth){int gems=Math.max(0,emeraldCost);float safeMin=Math.max(.5f,minimumHealth);float raw=(float)Math.floor(Math.max(0,health)*Math.max(0,Math.min(1,healthFraction)));float hp=Math.max(0,Math.min(raw,Math.max(0,health-safeMin)));if(emeralds<gems)return new Quote(gems,0,health,false,"missing_emeralds");if(health<=safeMin)return new Quote(gems,0,health,false,"insufficient_health");return new Quote(gems,hp,health-hp,true,"ok");}
}
