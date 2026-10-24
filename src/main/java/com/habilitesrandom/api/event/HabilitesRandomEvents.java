package com.habilitesrandom.api.event;
import com.habilitesrandom.api.*;import com.habilitesrandom.api.execution.AbilityExecutionResult;import net.minecraft.server.level.ServerPlayer;import java.util.concurrent.CopyOnWriteArrayList;import java.util.function.Consumer;
/** Server-side, observation-only events. Listener failure is isolated by Core dispatch. */
public final class HabilitesRandomEvents {private HabilitesRandomEvents(){}
 public record CharacterAssigned(ServerPlayer player,CharacterDefinition character,Reason reason){public enum Reason{INITIAL_RANDOM,REROLL}}
 public record AbilityUsed(ServerPlayer player,AbilityDefinition ability,int slot,AbilityExecutionResult result){}
 public record FormActivated(ServerPlayer player,TransformationDefinition form){}
 public record FormEnded(ServerPlayer player,TransformationDefinition form,String reason){}
 public static final Event<CharacterAssigned> CHARACTER_ASSIGNED=new Event<>();public static final Event<AbilityUsed> ABILITY_USED=new Event<>();public static final Event<FormActivated> FORM_ACTIVATED=new Event<>();public static final Event<FormEnded> FORM_ENDED=new Event<>();
 public static final class Event<T>{private final CopyOnWriteArrayList<Consumer<T>> listeners=new CopyOnWriteArrayList<>();public AutoCloseable register(Consumer<T> listener){if(listener==null)throw new IllegalArgumentException("listener");listeners.add(listener);return()->listeners.remove(listener);}public void dispatch(T event){for(var listener:listeners)try{listener.accept(event);}catch(RuntimeException ignored){}}public int listenerCount(){return listeners.size();}}
}
