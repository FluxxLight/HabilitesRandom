package com.habilitesrandom;

import com.habilitesrandom.data.PlayerProfile;
import com.mojang.serialization.JsonOps;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerProfileCodecsTest {
    @Test
    void emptyProfileRoundTrips() {
        PlayerProfile original = PlayerProfile.empty(1234L);
        var encoded = PlayerProfile.CODEC.encodeStart(JsonOps.INSTANCE, original).result().orElseThrow();
        PlayerProfile decoded = PlayerProfile.CODEC.parse(JsonOps.INSTANCE, encoded).result().orElseThrow();

        assertEquals(PlayerProfile.SCHEMA_VERSION, decoded.schema());
        assertEquals(1234L, decoded.createdAtEpochMillis());
        assertFalse(decoded.hasCharacter());
        assertEquals(PlayerProfile.RerollState.UNINITIALIZED, decoded.progress().rerollState());
        assertTrue(decoded.loadout().abilityIds().isEmpty());
    }

    @Test
    void futureProfileFieldsArePreserved() {
        PlayerProfile.Identity identity = new PlayerProfile.Identity(
                "plus:hero", "plus:series", "plus:mythic", "strong", "FORTE");
        PlayerProfile.Loadout loadout = new PlayerProfile.Loadout(
                java.util.List.of("plus:one", "plus:two"), "plus:form", 3, true, 2, 7);
        PlayerProfile.Cooldowns cooldowns = new PlayerProfile.Cooldowns(1000, 2000, 3000, 4000, 5000);
        PlayerProfile.Progress progress = new PlayerProfile.Progress(1, false, 55, true,
                "minecraft:overworld", 10, 64, -20);
        PlayerProfile original = PlayerProfile.create(1, identity, progress, loadout, cooldowns, 100L, 200L);

        var encoded = PlayerProfile.CODEC.encodeStart(JsonOps.INSTANCE, original).result().orElseThrow();
        PlayerProfile decoded = PlayerProfile.CODEC.parse(JsonOps.INSTANCE, encoded).result().orElseThrow();

        assertTrue(decoded.hasCharacter());
        assertEquals("plus:hero", decoded.identity().characterId());
        assertEquals("strong", decoded.identity().category());
        assertEquals(2, decoded.loadout().abilityIds().size());
        assertEquals(1, decoded.progress().rerollsRemaining());
        assertTrue(decoded.progress().sanctuaryDiscovered());
        assertEquals(PlayerProfile.RerollState.AVAILABLE, decoded.progress().rerollState());
        assertEquals(2, decoded.cooldowns().remainingSeconds(1000)[1]);
    }

    @Test
    void invalidIdentifiersAreSanitizedAway() {
        PlayerProfile.Identity identity = new PlayerProfile.Identity("Not A Valid ID!", null, null, "x", null);
        assertEquals("", identity.characterId());
        assertEquals("unknown", identity.category());
    }
}
