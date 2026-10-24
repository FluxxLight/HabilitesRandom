package com.habilitesrandom.api.execution;

import com.habilitesrandom.api.AbilityDefinition;
import net.minecraft.server.level.ServerPlayer;

/** Trusted context created by Core after character, slot, cooldown and packet validation. */
public record AbilityExecutionContext(ServerPlayer player, AbilityDefinition ability, int slot, boolean powerfulFormActive) {
    public AbilityExecutionContext {
        if (player == null || ability == null || slot < 0 || slot > 3) throw new IllegalArgumentException("Invalid execution context");
    }
}
