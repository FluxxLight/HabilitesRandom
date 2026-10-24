package com.habilitesrandom.randomizer;
import java.util.*; import java.util.random.RandomGenerator; import java.util.function.ToDoubleFunction;
/** Stable weighted selection with finite-value validation and no fixed content list. */
public final class WeightedRandomSelector { private WeightedRandomSelector(){}
 public static <T> T select(List<T> values, ToDoubleFunction<T> weight, RandomGenerator random){
  Objects.requireNonNull(values); Objects.requireNonNull(weight); Objects.requireNonNull(random);
  if(values.isEmpty()) throw new IllegalArgumentException("Cannot select from an empty catalog");
  double total=0; for(T value:values){double w=weight.applyAsDouble(value);if(!Double.isFinite(w)||w<0)throw new IllegalArgumentException("Invalid weight");total+=w;}
  if(!(total>0)||!Double.isFinite(total))throw new IllegalArgumentException("Total weight must be finite and positive");
  double cursor=random.nextDouble(total); T last=values.getLast();
  for(T value:values){double w=weight.applyAsDouble(value);if(cursor<w)return value;cursor-=w;if(w>0)last=value;}return last;
 }
 public static <T> double chance(T value,List<T> values,ToDoubleFunction<T> weight){double total=values.stream().mapToDouble(weight).sum();return total<=0?0:weight.applyAsDouble(value)/total;}
}
