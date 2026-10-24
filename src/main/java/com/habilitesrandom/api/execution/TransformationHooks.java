package com.habilitesrandom.api.execution;
import com.habilitesrandom.api.TransformationDefinition;import net.minecraft.server.level.ServerPlayer;
/** Optional Plus callbacks. Core still owns charges, duration, persistence and cleanup. */
public interface TransformationHooks {
 default void onActivated(ServerPlayer player, TransformationDefinition definition) {}
 default void onEnded(ServerPlayer player, TransformationDefinition definition, EndReason reason) {}
 enum EndReason { EXPIRED, DEATH, REROLL, DISCONNECT_RECONCILIATION }
}
