package com.habilitesrandom.client.net;

import com.habilitesrandom.network.ProfileSyncPayload;
import com.habilitesrandom.util.ProfileValues;

/** Read-only client mirror of the last server snapshot. Never used as gameplay authority. */
public final class ClientProfileCache {
    private static volatile ProfileSyncPayload snapshot = ProfileSyncPayload.empty();
    private static volatile int[] cooldownSeconds = new int[ProfileValues.MAX_ABILITIES];
    private static volatile long receivedAt = 0L;
    private static volatile boolean linked;

    private ClientProfileCache() {
    }

    public static void accept(ProfileSyncPayload payload) {
        snapshot = payload == null ? ProfileSyncPayload.empty() : payload;
        cooldownSeconds = ProfileValues.parseCooldowns(snapshot.progress().cooldownSeconds());
        receivedAt = System.currentTimeMillis();
        linked = true;
    }

    public static void clear() {
        snapshot = ProfileSyncPayload.empty();
        cooldownSeconds = new int[ProfileValues.MAX_ABILITIES];
        receivedAt = 0L;
        linked = false;
    }

    public static ProfileSyncPayload get() {
        return snapshot;
    }

    public static boolean isLinked() {
        return linked;
    }

    public static boolean hasCharacter() {
        return snapshot.core().hasCharacter();
    }

    public static String characterId() { return snapshot.core().characterId(); }

    public static String characterName() {
        String name = snapshot.core().characterName();
        return name.isBlank() ? snapshot.core().characterId() : name;
    }

    public static String animeName() {
        return snapshot.core().animeName();
    }

    public static String rarityName() {
        return snapshot.core().rarityName();
    }

    public static String category() {
        return snapshot.core().category();
    }

    public static String rerollState() {
        return snapshot.progress().rerollState();
    }

    public static int transformationUses() {
        return snapshot.progress().transformationUses();
    }

    public static String transformationName() {
        return snapshot.progress().transformationName();
    }

    public static boolean powerfulFruitActive() {
        return snapshot.progress().powerfulFruitActive();
    }

    public static int sanctuaryProgress() {
        return snapshot.progress().sanctuaryProgress();
    }

    public static String sanctuaryHint() {
        return snapshot.progress().sanctuaryHint();
    }

    public static int cooldownRemaining(int slot) {
        if (slot < 0 || slot >= cooldownSeconds.length) {
            return 0;
        }
        int elapsed = receivedAt == 0L ? 0 : (int) ((System.currentTimeMillis() - receivedAt) / 1000L);
        return Math.max(0, cooldownSeconds[slot] - elapsed);
    }
}
