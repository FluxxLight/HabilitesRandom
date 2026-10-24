package com.habilitesrandom.data;

import com.habilitesrandom.HabilitesRandom;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** World-level saved data keyed by UUID; death and respawn never alter its identity. */
public final class PlayerProfileState extends SavedData {
    public static final Codec<PlayerProfileState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(UUIDUtil.STRING_CODEC, PlayerProfile.CODEC)
                    .optionalFieldOf("players", Map.of())
                    .forGetter(state -> Map.copyOf(state.players))
    ).apply(instance, PlayerProfileState::new));

    public static final SavedDataType<PlayerProfileState> TYPE = new SavedDataType<>(
            HabilitesRandom.id("player_profiles"),
            PlayerProfileState::new,
            CODEC,
            // Vanilla treats this DFU type as opaque, so our additive fields are never rewritten.
            DataFixTypes.SAVED_DATA_COMMAND_STORAGE
    );

    private final Map<UUID, PlayerProfile> players = new ConcurrentHashMap<>();

    public PlayerProfileState() {
    }

    private PlayerProfileState(Map<UUID, PlayerProfile> loaded) {
        if (loaded != null) {
            players.putAll(loaded);
        }
    }

    public PlayerProfile get(UUID playerId) {
        return players.getOrDefault(playerId, PlayerProfile.empty());
    }

    public boolean contains(UUID playerId) {
        return players.containsKey(playerId);
    }

    public void put(UUID playerId, PlayerProfile profile) {
        players.put(playerId, profile);
        setDirty();
    }

    public int size() {
        return players.size();
    }

    public Map<UUID, PlayerProfile> view() {
        return Collections.unmodifiableMap(players);
    }
}
