package com.habilitesrandom.screen;

import com.habilitesrandom.client.net.ClientNetworking;
import com.habilitesrandom.client.net.ClientProfileCache;
import com.habilitesrandom.client.ui.HubButton;
import com.habilitesrandom.client.ui.UiAnimation;
import com.habilitesrandom.client.ui.UiLayout;
import com.habilitesrandom.client.ui.UiText;
import com.habilitesrandom.client.ui.UiTheme;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

/** Main navigation HUB with a responsive hero and a six-entry command deck. */
public final class HubScreen extends AbstractHubScreen {
    private UiLayout.Rect hero;
    private UiLayout.Rect menu;

    public HubScreen(Screen parent) {
        super(Component.translatable("habilitesrandom.hub.title"), parent);
    }

    @Override
    protected Component subtitle() {
        return Component.translatable("habilitesrandom.hub.subtitle");
    }

    @Override
    protected int footerHeight() {
        return 24;
    }

    @Override
    protected void buildContent() {
        UiLayout.Rect content = content();
        boolean wide = content.width() >= 640 && content.height() >= 270;
        if (wide) {
            int heroWidth = content.width() * 39 / 100;
            hero = new UiLayout.Rect(content.x(), content.y(), heroWidth, content.height());
            menu = new UiLayout.Rect(hero.right() + 14, content.y(),
                    content.width() - heroWidth - 14, content.height());
        } else {
            int heroHeight = UiLayout.clamp(content.height() * 25 / 100, 42, 118);
            hero = new UiLayout.Rect(content.x(), content.y(), content.width(), heroHeight);
            menu = new UiLayout.Rect(content.x(), hero.bottom() + 7, content.width(),
                    Math.max(48, content.bottom() - hero.bottom() - 7));
        }

        List<UiLayout.Rect> slots = UiLayout.menuSlots(menu, 6);
        addEntry(slots, 0, "habilitesrandom.hub.randomizer", "habilitesrandom.hub.randomizer.subtitle",
                UiTheme.CYAN, () -> navigate(new RandomizerScreen(this)), 3);
        addEntry(slots, 1, "habilitesrandom.hub.ability", "habilitesrandom.hub.ability.subtitle",
                UiTheme.PURPLE, () -> navigate(new AbilityScreen(this)), 2);
        addEntry(slots, 2, "habilitesrandom.hub.reroll", "habilitesrandom.hub.reroll.subtitle",
                UiTheme.GOLD, () -> navigate(new RerollScreen(this)), 4);
        addEntry(slots, 3, "habilitesrandom.hub.sanctuary", "habilitesrandom.hub.sanctuary.subtitle",
                UiTheme.GREEN, () -> navigate(new SanctuaryScreen(this)), 4);
        addEntry(slots, 4, "habilitesrandom.hub.settings", "habilitesrandom.hub.settings.subtitle",
                UiTheme.CYAN, () -> navigate(new SettingsScreen(this)), 1);
        addEntry(slots, 5, "habilitesrandom.hub.close", "habilitesrandom.hub.close.subtitle",
                UiTheme.RED, this::onClose, 1);
    }

    private void addEntry(List<UiLayout.Rect> slots, int index, String titleKey, String subtitleKey,
                          int accent, Runnable action, int prompt) {
        UiLayout.Rect slot = slots.get(index);
        Component tooltip = prompt > 1
                ? Component.translatable("habilitesrandom.common.available_prompt", prompt)
                : null;
        addRenderableWidget(new HubButton(slot.x(), slot.y(), slot.width(), slot.height(),
                Component.translatable(titleKey), Component.translatable(subtitleKey), accent,
                true, action, tooltip));
    }

    @Override
    protected void drawContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (hero == null || menu == null) {
            return;
        }
        drawHero(graphics);
        UiTheme.card(graphics, menu.x(), menu.y(), menu.width(), menu.height(), UiTheme.PURPLE, 0.38F);
        if (menu.height() >= 84) {
            UiText.label(graphics, font, ClientNetworking.serverHasMod()
                            ? Component.translatable("habilitesrandom.hub.server").getString()
                            : Component.translatable("habilitesrandom.hub.client").getString(),
                    menu.x() + 10, menu.y() + 6, menu.width() - 20, UiTheme.TEXT_DIM);
        }
    }

    private void drawHero(GuiGraphicsExtractor graphics) {
        UiTheme.card(graphics, hero.x(), hero.y(), hero.width(), hero.height(), UiTheme.CYAN, 0.78F);
        int iconSize = Math.max(24, Math.min(hero.width() - 20, hero.height() - 28));
        int iconX = hero.x() + (hero.width() - iconSize) / 2;
        int iconY = hero.y() + 6;
        UiTheme.icon(graphics, iconX, iconY, iconSize);

        long now = System.currentTimeMillis();
        float pulse = UiAnimation.pulse(now, 2400L);
        int ringColor = UiTheme.alpha(UiTheme.PURPLE, 0.25F + pulse * 0.35F);
        graphics.fill(hero.x() + 5, iconY + iconSize / 2,
                hero.x() + hero.width() - 5, iconY + iconSize / 2 + 1, ringColor);

        int footerY = hero.bottom() - 25;
        UiTheme.divider(graphics, hero.x() + 8, footerY - 6, hero.width() - 16, 0.8F);
        String status = Component.translatable("habilitesrandom.hub.status").getString();
        UiText.label(graphics, font, status, hero.x() + 9, footerY, hero.width() - 18, UiTheme.GREEN);
        String profile = ClientProfileCache.hasCharacter()
                ? ClientProfileCache.characterName()
                : Component.translatable("habilitesrandom.ability.no_character").getString();
        UiText.label(graphics, font, profile, hero.x() + 9, footerY + 10,
                hero.width() - 18, UiTheme.TEXT_SOFT);
    }
}
