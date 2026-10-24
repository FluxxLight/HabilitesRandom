package com.habilitesrandom.config.server;
import java.util.*;
/** Server-owned gameplay policy. Empty allow lists mean all registered values are allowed. */
public final class ServerConfig {
 public boolean randomizerEnabled=true, antiLuckEnabled=true, announceResults=true, abilityExecutionEnabled=true, blockDestruction=false;
 public String announcementMode="GLOBAL";
 public Set<String> blockedCharacters=new HashSet<>(), enabledRarities=new HashSet<>(), enabledCategories=new HashSet<>(), allowedDimensions=new HashSet<>();
 public Map<String,Double> characterWeightOverrides=new HashMap<>();
 public int packetIntervalMillis=120, maxTargets=64, maxBlocksPerAbility=128;
 public void sanitize(){packetIntervalMillis=Math.max(75,Math.min(packetIntervalMillis,2000));maxTargets=Math.max(1,Math.min(maxTargets,256));maxBlocksPerAbility=Math.max(0,Math.min(maxBlocksPerAbility,512));if(announcementMode==null)announcementMode="GLOBAL";blockedCharacters.removeIf(Objects::isNull);characterWeightOverrides.entrySet().removeIf(e->e.getKey()==null||e.getValue()==null||!Double.isFinite(e.getValue())||e.getValue()<=0);}
}
