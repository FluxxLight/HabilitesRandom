package com.habilitesrandom.client;

import com.habilitesrandom.client.hud.HabilitesRandomHud;
import com.habilitesrandom.client.input.ModKeyBindings;
import com.habilitesrandom.client.net.ClientNetworking;
import com.habilitesrandom.config.ClientConfig;
import com.habilitesrandom.config.ClientConfigStore;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

/** Client entrypoint owns presentation state only. */
public final class HabilitesRandomClient implements ClientModInitializer {
    private static ClientConfigStore configStore;

    @Override
    public void onInitializeClient() {
        configStore = new ClientConfigStore();
        ModKeyBindings.register();
        ClientNetworking.register();
        HabilitesRandomHud.register();
        HubController.register();

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> saveConfig());
    }

    public static ClientConfig config() {
        if (configStore == null) {
            configStore = new ClientConfigStore();
        }
        return configStore.get();
    }

    public static void saveConfig() {
        if (configStore != null) {
            configStore.save();
        }
    }
}
