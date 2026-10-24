package com.habilitesrandom.config.server;
import java.util.*;
/** Server-owned gameplay policy. Empty allow lists mean all registered values are allowed. */
public final class ServerConfig {
 public boolean randomizerEnabled=true, antiLuckEnabled=true, announceResults=true, abilityExecutionEnabled=true, blockDestruction=false, allowPowerfulFormTerrainDamage=false, sanctuaryEnabled=true, sanctuaryVillageGeneration=true, sanctuaryEffects=true, sanctuaryDeathProtection=true;
 public String announcementMode="GLOBAL";
 public Set<String> blockedCharacters=new HashSet<>(), enabledRarities=new HashSet<>(), enabledCategories=new HashSet<>(), allowedDimensions=new HashSet<>();
 public Map<String,Double> characterWeightOverrides=new HashMap<>();
 public int packetIntervalMillis=120, maxTargets=64, maxBlocksPerAbility=128, sanctuaryEmeraldCost=15, sanctuaryMinDistance=600, sanctuaryMaxDistance=1800;
 public double sanctuaryHealthFraction=0.5;
 public void sanitize(){packetIntervalMillis=Math.max(75,Math.min(packetIntervalMillis,2000));maxTargets=Math.max(1,Math.min(maxTargets,256));maxBlocksPerAbility=Math.max(0,Math.min(maxBlocksPerAbility,512));sanctuaryEmeraldCost=Math.max(0,Math.min(sanctuaryEmeraldCost,64));sanctuaryHealthFraction=Math.max(0,Math.min(sanctuaryHealthFraction,1));sanctuaryMinDistance=Math.max(128,sanctuaryMinDistance);sanctuaryMaxDistance=Math.max(sanctuaryMinDistance,Math.min(sanctuaryMaxDistance,10000));if(announcementMode==null)announcementMode="GLOBAL";blockedCharacters.removeIf(Objects::isNull);characterWeightOverrides.entrySet().removeIf(e->e.getKey()==null||e.getValue()==null||!Double.isFinite(e.getValue())||e.getValue()<=0);}
}
