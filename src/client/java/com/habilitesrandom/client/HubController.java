package com.habilitesrandom.client;

import com.habilitesrandom.client.input.ModKeyBindings;
import com.habilitesrandom.client.net.ClientNetworking;
import com.habilitesrandom.client.net.ClientProfileCache;
import com.habilitesrandom.config.ClientConfig;
import com.habilitesrandom.screen.HubScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

/** Opens the HUB once after world join, keeps the H key responsive and clears client state on leave. */
public final class HubController {
    private static boolean pendingAutoOpen;
    private static int ticksSinceJoin;
    private static boolean hintShown;

    private HubController() {
    }

    public static void register() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            ticksSinceJoin = 0;
            hintShown = false;
            pendingAutoOpen = HabilitesRandomClient.config().openHubOnJoin;
            ClientNetworking.requestProfile();
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            pendingAutoOpen = false;
            ticksSinceJoin = 0;
            ClientProfileCache.clear();
        });
        ClientTickEvents.END_CLIENT_TICK.register(HubController::tick);
    }

    public static void openHub(Minecraft client) {
        if (client.player != null && client.level != null) {
            client.setScreenAndShow(new HubScreen(null));
        }
    }

    private static void tick(Minecraft client) {
        if (client.player == null || client.level == null) {
            return;
        }
        ticksSinceJoin++;
        if (ticksSinceJoin == 20 || ticksSinceJoin == 80) {
            // Retry once for servers whose channel registry arrives slightly after the join event.
            ClientNetworking.requestProfile();
        }

        if (client.gui.screen() == null) {
            if (pendingAutoOpen && ticksSinceJoin >= 20) {
                pendingAutoOpen = false;
                openHub(client);
                return;
            }
            if (!hintShown && ticksSinceJoin >= 45) {
                hintShown = true;
                ClientConfig config = HabilitesRandomClient.config();
                if (config.showHubHint && !config.openHubOnJoin) {
                    client.gui.hud.setOverlayMessage(Component.translatable(
                            "habilitesrandom.hub.hint", ModKeyBindings.hubLabel()), false);
                }
            }
        }

        while (ModKeyBindings.HUB.consumeClick()) {
            if (client.gui.screen() == null) {
                openHub(client);
            }
        }

        // Prompt 3 will send validated action requests. Consuming now keeps the mappings inert and clean.
        for (int slot = 0; slot < 5; slot++) {
            while (ModKeyBindings.globalAction(slot).consumeClick()) {
                // No gameplay action exists yet; the server would validate it in Prompt 3.
            }
        }
    }
}
