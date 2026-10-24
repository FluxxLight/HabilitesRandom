package com.habilitesrandom.screen;

import com.habilitesrandom.client.HabilitesRandomClient;
import com.habilitesrandom.client.input.ModKeyBindings;
import com.habilitesrandom.client.ui.HubButton;
import com.habilitesrandom.client.ui.UiLayout;
import com.habilitesrandom.client.ui.UiText;
import com.habilitesrandom.client.ui.UiTheme;
import com.habilitesrandom.config.ClientConfig;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/** Five category tabs, persisted toggles and real global key remapping. */
public final class SettingsScreen extends AbstractHubScreen {
    private enum Category {
        CONTROLS("habilitesrandom.settings.controls", UiTheme.CYAN),
        INTERFACE("habilitesrandom.settings.interface", UiTheme.PURPLE),
        EFFECTS("habilitesrandom.settings.effects", UiTheme.GOLD),
        AUDIO("habilitesrandom.settings.audio", UiTheme.GREEN),
        GENERAL("habilitesrandom.settings.general", UiTheme.RED);

        private final String titleKey;
        private final int accent;

        Category(String titleKey, int accent) {
            this.titleKey = titleKey;
            this.accent = accent;
        }
    }

    private Category category = Category.CONTROLS;
    private int listeningSlot = -1;
    private UiLayout.Rect rail;
    private UiLayout.Rect body;

    public SettingsScreen(Screen parent) {
        super(Component.translatable("habilitesrandom.page.settings"), parent);
    }

    @Override
    protected Component subtitle() {
        return Component.translatable(category.titleKey);
    }

    @Override
    protected void buildContent() {
        UiLayout.Rect content = content();
        boolean sideRail = content.width() >= 600 && content.height() >= 280;
        if (sideRail) {
            int railWidth = UiLayout.clamp(content.width() * 23 / 100, 130, 180);
            rail = new UiLayout.Rect(content.x(), content.y(), railWidth, content.height());
            body = new UiLayout.Rect(rail.right() + 10, content.y(),
                    content.width() - railWidth - 10, content.height());
        } else {
            int railHeight = 28;
            rail = new UiLayout.Rect(content.x(), content.y(), content.width(), railHeight);
            body = new UiLayout.Rect(content.x(), rail.bottom() + 7, content.width(),
                    Math.max(60, content.bottom() - rail.bottom() - 7));
        }
        buildRail(sideRail);
        buildBody();
        addFooterButton(Component.translatable("habilitesrandom.common.back"), this::onClose);
    }

    private void buildRail(boolean sideRail) {
        Category[] categories = Category.values();
        List<UiLayout.Rect> slots = sideRail
                ? UiLayout.splitRows(rail, categories.length, 6)
                : UiLayout.splitColumns(rail, categories.length, 4);
        for (int index = 0; index < categories.length; index++) {
            Category item = categories[index];
            UiLayout.Rect slot = slots.get(index);
            HubButton button = HubButton.compact(slot.x(), slot.y(), slot.width(), slot.height(),
                    Component.translatable(item.titleKey), item.accent, true, () -> {
                        category = item;
                        listeningSlot = -1;
                        rebuildWidgets();
                    }, null);
            button.setSelected(category == item);
            addRenderableWidget(button);
        }
    }

    private void buildBody() {
        ClientConfig config = HabilitesRandomClient.config();
        int pad = 8;
        int descriptionHeight = UiLayout.clamp(body.height() * 16 / 100, 26, 44);
        int rowGap = 6;
        int rowCount = switch (category) {
            case CONTROLS -> 7;
            case INTERFACE -> 5;
            case EFFECTS -> 2;
            case AUDIO -> 2;
            case GENERAL -> 3;
        };
        int rowsY = body.y() + descriptionHeight;
        int rowsHeight = Math.max(40, body.bottom() - rowsY - pad);
        List<UiLayout.Rect> rows = UiLayout.splitRows(
                new UiLayout.Rect(body.x() + pad, rowsY, body.width() - 2 * pad, rowsHeight),
                rowCount, rowGap);

        switch (category) {
            case CONTROLS -> {
                addKeyRow(rows.get(0), 0);
                addKeyRow(rows.get(1), 1);
                addKeyRow(rows.get(2), 2);
                addKeyRow(rows.get(3), 3);
                addKeyRow(rows.get(4), 4);
                List<UiLayout.Rect> actions = UiLayout.splitColumns(
                        new UiLayout.Rect(rows.get(5).x(), rows.get(5).y(), rows.get(5).width(),
                                rows.get(6).bottom() - rows.get(5).y()), 2, 8);
                addRenderableWidget(HubButton.compact(actions.get(0).x(), actions.get(0).y(),
                        actions.get(0).width(), actions.get(0).height(),
                        Component.translatable("habilitesrandom.settings.controls.reset"),
                        UiTheme.GOLD, true, () -> {
                            ModKeyBindings.resetDefaults();
                            listeningSlot = -1;
                            rebuildWidgets();
                        }, null));
                addRenderableWidget(HubButton.compact(actions.get(1).x(), actions.get(1).y(),
                        actions.get(1).width(), actions.get(1).height(),
                        Component.translatable("habilitesrandom.settings.controls.vanilla"),
                        UiTheme.CYAN, true,
                        () -> navigate(new KeyBindsScreen(this, minecraft.options)), null));
            }
            case INTERFACE -> {
                addToggleRow(rows.get(0), "habilitesrandom.settings.interface.particles",
                        () -> config.interfaceParticles, value -> config.interfaceParticles = value);
                addToggleRow(rows.get(1), "habilitesrandom.settings.interface.animations",
                        () -> config.interfaceAnimations, value -> config.interfaceAnimations = value);
                addToggleRow(rows.get(2), "habilitesrandom.settings.interface.hud",
                        () -> config.hudVisible, value -> config.hudVisible = value);
                addToggleRow(rows.get(3), "habilitesrandom.settings.interface.compact_hud",
                        () -> config.compactHud, value -> config.compactHud = value);
                addToggleRow(rows.get(4), "habilitesrandom.settings.interface.glow",
                        () -> config.hudGlow, value -> config.hudGlow = value);
            }
            case EFFECTS -> {
                addToggleRow(rows.get(0), "habilitesrandom.settings.effects.effects",
                        () -> config.visualEffects, value -> config.visualEffects = value);
                addRenderableWidget(new HubButton(rows.get(1).x(), rows.get(1).y(), rows.get(1).width(),
                        rows.get(1).height(),
                        Component.translatable("habilitesrandom.settings.effects.intensity"),
                        Component.translatable(config.intensity().translationKey()), UiTheme.GOLD, true,
                        button -> {
                            config.cycleIntensity();
                            HabilitesRandomClient.saveConfig();
                            rebuildWidgets();
                        }, null));
            }
            case AUDIO -> {
                addToggleRow(rows.get(0), "habilitesrandom.settings.audio.sounds",
                        () -> config.interfaceSounds, value -> config.interfaceSounds = value);
                addToggleRow(rows.get(1), "habilitesrandom.settings.audio.ambient",
                        () -> config.ambientUiAudio, value -> config.ambientUiAudio = value);
            }
            case GENERAL -> {
                addToggleRow(rows.get(0), "habilitesrandom.settings.general.hub_on_join",
                        () -> config.openHubOnJoin, value -> config.openHubOnJoin = value);
                addToggleRow(rows.get(1), "habilitesrandom.settings.general.hub_hint",
                        () -> config.showHubHint, value -> config.showHubHint = value);
                addToggleRow(rows.get(2), "habilitesrandom.settings.general.tooltips",
                        () -> config.advancedTooltips, value -> config.advancedTooltips = value);
            }
        }
    }

    private void addKeyRow(UiLayout.Rect rect, int slot) {
        KeyMapping mapping = ModKeyBindings.globalAction(slot);
        Component value = listeningSlot == slot
                ? Component.translatable("habilitesrandom.settings.controls.listening")
                : mapping.getTranslatedKeyMessage();
        boolean compact = rect.height() < 34;
        Component title = compact
                ? Component.translatable(mapping.getName()).copy().append(Component.literal("   ")).append(value)
                : Component.translatable(mapping.getName());
        Component subtitle = compact ? Component.empty() : value;
        HubButton button = new HubButton(rect.x(), rect.y(), rect.width(), rect.height(),
                title, subtitle, UiTheme.CYAN, true,
                trigger -> {
                    listeningSlot = slot;
                    rebuildWidgets();
                }, null);
        button.setSelected(listeningSlot == slot);
        addRenderableWidget(button);
    }

    private void addToggleRow(UiLayout.Rect rect, String labelKey, Supplier<Boolean> getter,
                              Consumer<Boolean> setter) {
        boolean value = Boolean.TRUE.equals(getter.get());
        Component state = Component.literal(value ? "ON" : "OFF");
        boolean compact = rect.height() < 34;
        Component title = compact
                ? Component.translatable(labelKey).copy().append(Component.literal("   ")).append(state)
                : Component.translatable(labelKey);
        Component subtitle = compact ? Component.empty() : state;
        HubButton button = new HubButton(rect.x(), rect.y(), rect.width(), rect.height(),
                title, subtitle,
                value ? UiTheme.GREEN : UiTheme.RED, true, trigger -> {
                    setter.accept(!Boolean.TRUE.equals(getter.get()));
                    HabilitesRandomClient.saveConfig();
                    rebuildWidgets();
                }, null);
        button.setSelected(value);
        addRenderableWidget(button);
    }

    @Override
    protected void drawContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (rail == null || body == null) {
            return;
        }
        UiTheme.card(graphics, rail.x(), rail.y(), rail.width(), rail.height(), category.accent, 0.45F);
        UiTheme.card(graphics, body.x(), body.y(), body.width(), body.height(), category.accent, 0.66F);
        int pad = 8;
        Component description = category == Category.CONTROLS
                ? Component.translatable("habilitesrandom.settings.controls.description")
                : category == Category.GENERAL
                ? Component.translatable("habilitesrandom.settings.general.tooltips.body")
                : Component.translatable(category.titleKey);
        int descriptionHeight = UiLayout.clamp(body.height() * 16 / 100, 26, 44);
        graphics.enableScissor(body.x() + pad, body.y() + 4, body.right() - pad,
                Math.min(body.bottom(), body.y() + descriptionHeight - 6));
        UiText.wrapped(graphics, font, description, body.x() + pad, body.y() + 7,
                body.width() - 2 * pad, UiTheme.TEXT_SOFT, 2);
        graphics.disableScissor();
        UiTheme.divider(graphics, body.x() + pad, body.y() + descriptionHeight - 5,
                body.width() - 2 * pad, 0.7F);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (listeningSlot >= 0) {
            InputConstants.Key key = event.isEscape() ? InputConstants.UNKNOWN : InputConstants.getKey(event);
            ModKeyBindings.applyRemap(ModKeyBindings.globalAction(listeningSlot), key);
            listeningSlot = -1;
            HabilitesRandomClient.saveConfig();
            rebuildWidgets();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (listeningSlot >= 0 && event.button() >= 0) {
            ModKeyBindings.applyRemap(ModKeyBindings.globalAction(listeningSlot),
                    InputConstants.Type.MOUSE.getOrCreate(event.button()));
            listeningSlot = -1;
            HabilitesRandomClient.saveConfig();
            rebuildWidgets();
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    protected void onBeforeClose() {
        listeningSlot = -1;
        HabilitesRandomClient.saveConfig();
    }
}
