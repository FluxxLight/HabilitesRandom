package com.habilitesrandom.sanctuary;
import com.habilitesrandom.data.PlayerProfile;
/** Per-player eligibility and reward transitions; the shared sanctuary never stores global usage. */
public final class SanctuaryService {private SanctuaryService(){}
 public static boolean mapEligible(PlayerProfile p){return p.hasCharacter()&&p.progress().freeRerollUsed()&&!p.progress().sanctuaryRerollAvailable()&&!p.progress().sanctuaryRerollUsed()&&!p.progress().sanctuaryMapPurchased();}
 public static boolean altarEligible(PlayerProfile p){return p.hasCharacter()&&p.progress().freeRerollUsed()&&p.progress().sanctuaryMapPurchased()&&!p.progress().sanctuaryRerollAvailable()&&!p.progress().sanctuaryRerollUsed();}
 public static PlayerProfile purchaseMap(PlayerProfile p,String dimension,int x,int y,int z){if(!mapEligible(p))throw new IllegalStateException("map_not_eligible");return p.withProgress(p.progress().withMap(dimension,x,y,z));}
 public static PlayerProfile claimFinalReroll(PlayerProfile p){if(!altarEligible(p))throw new IllegalStateException("altar_not_eligible");return p.withProgress(p.progress().grantSanctuaryReroll());}
}
