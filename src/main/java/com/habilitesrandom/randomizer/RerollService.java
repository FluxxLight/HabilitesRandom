package com.habilitesrandom.randomizer;
import com.habilitesrandom.data.PlayerProfile;import java.util.random.RandomGenerator;
/** Atomic reroll state transition layered on the one shared weighted randomizer. */
public final class RerollService {private RerollService(){}
 public record Result(RandomizerService.Result draw,PlayerProfile profile,Kind consumed){} public enum Kind{FREE,SANCTUARY}
 public static Result reroll(PlayerProfile p,RandomGenerator random){Kind kind=switch(p.progress().rerollState()){case FREE_AVAILABLE->Kind.FREE;case SANCTUARY_AVAILABLE->Kind.SANCTUARY;default->throw new IllegalStateException("no_reroll_available");};var draw=RandomizerService.reroll(p,random);var progress=kind==Kind.FREE?p.progress().consumeFreeReroll():p.progress().consumeSanctuaryReroll();PlayerProfile finalProfile=draw.profile().withProgress(progress);return new Result(draw,finalProfile,kind);}
}
