package com.habilitesrandom;

import com.habilitesrandom.config.ClientConfig;
import com.habilitesrandom.data.PlayerProfile;
import com.habilitesrandom.player.PlayerAuthorityGate;
import com.habilitesrandom.util.ProfileValues;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class SecurityAndConfigTest {
    @Test
    void allPrematureGameplayActionsAreDenied() {
        var decision = PlayerAuthorityGate.validate(null, PlayerProfile.empty(),
                PlayerAuthorityGate.Action.RANDOMIZE, 0);
        assertFalse(decision.allowed());
        assertEquals("missing_server_context", decision.reason());
    }

    @Test
    void corruptedCooldownPayloadNeverCrashesTheClient() {
        assertArrayEquals(new int[]{0, 0, 0, 0}, ProfileValues.parseCooldowns(null));
        assertArrayEquals(new int[]{4, 0, 2, 0}, ProfileValues.parseCooldowns("4,broken,2"));
        assertEquals("1,2,3,4", ProfileValues.joinCooldowns(new int[]{1, 2, 3, 4}));
    }

    @Test
    void clientConfigIsNormalized() {
        ClientConfig config = new ClientConfig();
        config.version = 99;
        config.effectIntensity = "not-a-level";
        config.sanitize();
        assertEquals(ClientConfig.CURRENT_VERSION, config.version);
        assertEquals(ClientConfig.EffectIntensity.MEDIUM, config.intensity());
    }
}
