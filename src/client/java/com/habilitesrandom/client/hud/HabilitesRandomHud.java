package com.habilitesrandom.client.hud;

import com.habilitesrandom.HabilitesRandom;
import com.habilitesrandom.client.HabilitesRandomClient;
import com.habilitesrandom.client.input.ModKeyBindings;
import com.habilitesrandom.client.net.ClientProfileCache;
import com.habilitesrandom.client.ui.UiText;
import com.habilitesrandom.client.ui.UiTheme;
import com.habilitesrandom.config.ClientConfig;
import com.habilitesrandom.screen.AbstractHubScreen;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/** Small, optional HUD shell. It reads only the last server-approved snapshot. */
public final class HabilitesRandomHud {
    private static final Identifier ID = HabilitesRandom.id("hud");

    private HabilitesRandomHud() {
    }

    public static void register() {
        HudElementRegistry.addLast(ID, HabilitesRandomHud::extract);
    }

    private static void extract(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft client = Minecraft.getInstance();
        ClientConfig config = HabilitesRandomClient.config();
        if (!config.hudVisible || client.player == null || client.gui.hud.isHidden()
                || client.gui.screen() instanceof AbstractHubScreen) {
            return;
        }

        boolean compact = config.compactHud;
        int width = compact ? 142 : 186;
        int height = compact ? 30 : 104;
        int x = graphics.guiWidth() - width - 8;
        int y = graphics.guiHeight() - height - 46;
        float opacity = config.hudGlow ? 0.92F : 0.78F;

        graphics.fillGradient(x, y, x + width, y + height,
                UiTheme.alpha(UiTheme.PANEL_TOP, opacity), UiTheme.alpha(UiTheme.PANEL_BOTTOM, opacity));
        UiTheme.corners(graphics, x, y, width, height, UiTheme.alpha(UiTheme.CYAN, opacity));
        graphics.fill(x, y, x + width, y + 1, UiTheme.alpha(UiTheme.BORDER, opacity));
        graphics.fill(x, y + height - 1, x + width, y + height, UiTheme.alpha(UiTheme.BORDER, opacity));

        Font font = client.font;
        int cx = x + 8;
        int cy = y + 6;
        UiText.label(graphics, font,
                Component.translatable("habilitesrandom.hud.title").getString(),
                cx, cy, width - 16, UiTheme.CYAN);
        cy += font.lineHeight + 3;

        if (compact) {
            String value = ClientProfileCache.hasCharacter() ? ClientProfileCache.characterName() : "—";
            UiText.label(graphics, font, value, cx, cy, width - 16, UiTheme.TEXT_SOFT);
            return;
        }

        String character = ClientProfileCache.hasCharacter()
                ? ClientProfileCache.characterName()
                : ClientProfileCache.isLinked() ? "—"
                : Component.translatable("habilitesrandom.hud.waiting_server").getString();
        UiText.label(graphics, font, character, cx, cy, width - 16, UiTheme.TEXT);
        cy += font.lineHeight + 2;

        String rarity = ClientProfileCache.rarityName();
        UiText.label(graphics, font,
                rarity.isBlank() ? Component.translatable("habilitesrandom.hud.no_rarity").getString() : rarity,
                cx, cy, width - 16, rarity.isBlank() ? UiTheme.TEXT_DIM : UiTheme.GOLD);
        cy += font.lineHeight + 4;

        for (int slot = 0; slot < 4; slot++) {
            int remaining = ClientProfileCache.cooldownRemaining(slot);
            String key = ModKeyBindings.actionLabel(slot).getString();
            UiText.label(graphics, font, key, cx, cy + 1, 24, UiTheme.TEXT_SOFT);
            int barX = cx + 28;
            int barWidth = width - 44;
            float value = remaining <= 0 ? 1.0F : Math.max(0.08F, Math.min(1.0F, remaining / 30.0F));
            int color = remaining <= 0 ? UiTheme.GREEN : UiTheme.CYAN;
            UiTheme.progress(graphics, barX, cy + 2, barWidth, 4, value, color, opacity);
            if (remaining > 0) {
                String seconds = Integer.toString(remaining);
                graphics.text(font, seconds, x + width - 7 - font.width(seconds), cy + 1,
                        UiTheme.TEXT_DIM, false);
            }
            cy += 10;
        }

        String form = ClientProfileCache.transformationName();
        String uses = Integer.toString(ClientProfileCache.transformationUses());
        UiText.label(graphics, font,
                form.isBlank() ? Component.translatable("habilitesrandom.hud.form_empty").getString() : form,
                cx, cy, width - 30, UiTheme.PURPLE);
        graphics.text(font, uses, x + width - 8 - font.width(uses), cy, UiTheme.TEXT_SOFT, false);
    }
}
