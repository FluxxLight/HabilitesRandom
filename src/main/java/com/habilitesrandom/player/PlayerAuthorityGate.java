package com.habilitesrandom.player;

import com.habilitesrandom.data.PlayerProfile;
import net.minecraft.server.level.ServerPlayer;

/**
 * The boundary every future gameplay action must cross. It intentionally denies actions in Prompt 1
 * rather than accepting a client claim about characters, cooldowns, rerolls or transformations.
 */
public final class PlayerAuthorityGate {
    private PlayerAuthorityGate() {
    }

    public enum Action {
        RANDOMIZE,
        REROLL,
        ABILITY,
        POWERFUL_FORM
    }

    public record Decision(boolean allowed, String reason) {
        public static Decision allow() {
            return new Decision(true, "ok");
        }

        public static Decision deny(String reason) {
            return new Decision(false, reason);
        }
    }

    public static Decision validate(ServerPlayer player, PlayerProfile profile, Action action, int slot) {
        if (player == null || profile == null) {
            return Decision.deny("missing_server_context");
        }
        if (player.isRemoved()) {
            return Decision.deny("removed_player");
        }
        if (action == Action.ABILITY && (slot < 0 || slot > 3)) {
            return Decision.deny("invalid_ability_slot");
        }

        // Prompt 2-5 replace these explicit denials with catalog, cooldown, reroll and form checks.
        return Decision.deny("not_implemented_server_side");
    }
}
