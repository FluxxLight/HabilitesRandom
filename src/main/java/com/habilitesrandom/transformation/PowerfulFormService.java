package com.habilitesrandom.transformation;
import com.habilitesrandom.api.TransformationDefinition;import com.habilitesrandom.data.PlayerProfile;
/** Server-owned daily charges keyed to the Overworld day number. */
public final class PowerfulFormService {private PowerfulFormService(){}
 public record Availability(int used,int limit,int remaining,boolean active,long activeMillis,long day){}
 public static Availability availability(PlayerProfile p,long day,long now){var l=p.loadout();int used=l.lastPowerfulFormDay()==day?l.transformationUses():0;int limit=l.powerfulFruitActive()?2:1;return new Availability(used,limit,Math.max(0,limit-used),l.powerfulFormActiveUntilMillis()>now,Math.max(0,l.powerfulFormActiveUntilMillis()-now),day);}
 public static PlayerProfile activate(PlayerProfile p,TransformationDefinition form,long day,long now){var a=availability(p,day,now);if(a.active())throw new IllegalStateException("form_already_active");if(a.remaining()<=0)throw new IllegalStateException("daily_form_exhausted");var l=p.loadout();return p.withLoadout(new PlayerProfile.Loadout(l.abilityIds(),l.transformationId(),a.used()+1,l.powerfulFruitActive(),l.powerfulFruitUses(),l.badLuckCounter(),day,now+form.durationTicks()*50L));}
 public static PlayerProfile finish(PlayerProfile p){var l=p.loadout();if(l.powerfulFormActiveUntilMillis()==0)return p;return p.withLoadout(new PlayerProfile.Loadout(l.abilityIds(),l.transformationId(),l.transformationUses(),l.powerfulFruitActive(),l.powerfulFruitUses(),l.badLuckCounter(),l.lastPowerfulFormDay(),0));}
 public static PlayerProfile consumeFruit(PlayerProfile p){var l=p.loadout();if(l.powerfulFruitActive())throw new IllegalStateException("fruit_already_active");return p.withLoadout(new PlayerProfile.Loadout(l.abilityIds(),l.transformationId(),l.transformationUses(),true,1,l.badLuckCounter(),l.lastPowerfulFormDay(),l.powerfulFormActiveUntilMillis()));}
 public static long overworldDay(long dayTime){return Math.floorDiv(dayTime,24000L);}
}
