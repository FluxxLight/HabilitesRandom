package com.habilitesrandom.randomizer;
import com.habilitesrandom.api.*;
/** Bounded category adjustment. It never changes rarity or guarantees a particular character. */
public final class AntiLuckPolicy {private AntiLuckPolicy(){}
 public static double multiplier(CharacterDefinition c,int weakStreak,boolean enabled){if(!enabled||c.category()!=StrengthCategory.STRONG)return 1;return weakStreak>=7?2.0:weakStreak>=5?1.55:weakStreak>=3?1.22:1;}
 public static int nextStreak(int current,CharacterDefinition result){return result.category()==StrengthCategory.STRONG?0:Math.min(1_000,current+1);}
}
