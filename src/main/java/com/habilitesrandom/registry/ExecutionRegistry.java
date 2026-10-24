package com.habilitesrandom.registry;
import com.habilitesrandom.api.execution.*;import net.minecraft.resources.Identifier;import java.util.*;import java.util.concurrent.ConcurrentHashMap;
/** Runtime strategies keyed by stable content ids; no character switch statements. */
public final class ExecutionRegistry {private final Map<Identifier,AbilityExecutor> abilities=new ConcurrentHashMap<>();private final Map<Identifier,TransformationHooks> forms=new ConcurrentHashMap<>();
 public boolean registerAbility(Identifier id,AbilityExecutor executor){return id!=null&&executor!=null&&abilities.putIfAbsent(id,executor)==null;}public Optional<AbilityExecutor> ability(Identifier id){return Optional.ofNullable(abilities.get(id));}
 public boolean registerTransformation(Identifier id,TransformationHooks hooks){return id!=null&&hooks!=null&&forms.putIfAbsent(id,hooks)==null;}public Optional<TransformationHooks> transformation(Identifier id){return Optional.ofNullable(forms.get(id));}}
