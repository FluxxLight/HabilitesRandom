package com.habilitesrandom.screen;

import com.habilitesrandom.client.input.ModKeyBindings;
import com.habilitesrandom.api.*;
import com.habilitesrandom.core.CoreServices;
import com.habilitesrandom.util.ProfileValues;
import com.habilitesrandom.client.net.ClientNetworking;
import com.habilitesrandom.client.net.ClientProfileCache;
import com.habilitesrandom.client.ui.HubButton;
import com.habilitesrandom.client.ui.UiLayout;
import com.habilitesrandom.client.ui.UiText;
import com.habilitesrandom.client.ui.UiTheme;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

/** Displays only empty/server-approved values; no fictitious character is ever shown as real data. */
public final class AbilityScreen extends AbstractHubScreen {
    public AbilityScreen(Screen parent) {
        super(Component.translatable("habilitesrandom.page.ability"), parent);
    }

    @Override
    protected Component subtitle() {
        return Component.translatable("habilitesrandom.hub.ability.subtitle");
    }

    @Override
    protected void buildContent() {
        addFooterButton(Component.translatable("habilitesrandom.common.back"), this::onClose);
    }

    @Override
    protected void drawContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        UiLayout.Rect content = content();
        UiTheme.card(graphics, content.x(), content.y(), content.width(), content.height(),
                UiTheme.PURPLE, 0.66F);

        if (!ClientProfileCache.hasCharacter()) {
            drawEmptyState(graphics, content);
            return;
        }
        drawProfile(graphics, content);
    }

    private void drawEmptyState(GuiGraphicsExtractor graphics, UiLayout.Rect content) {
        int boxWidth = Math.min(content.width() - 24, 430);
        int boxHeight = Math.min(content.height() - 24, 118);
        int x = content.x() + (content.width() - boxWidth) / 2;
        int y = content.y() + (content.height() - boxHeight) / 2;
        UiTheme.card(graphics, x, y, boxWidth, boxHeight, UiTheme.CYAN, 0.85F);
        UiText.centered(graphics, font,
                Component.translatable("habilitesrandom.ability.no_character").getString(),
                x + boxWidth / 2, y + 18, boxWidth - 24, UiTheme.GOLD);
        UiText.wrapped(graphics, font, Component.translatable("habilitesrandom.ability.no_character.body"),
                x + 16, y + 38, boxWidth - 32, UiTheme.TEXT_SOFT, 3);
        String authority = ClientNetworking.serverHasMod()
                ? Component.translatable("habilitesrandom.hub.server").getString()
                : Component.translatable("habilitesrandom.hub.client").getString();
        UiText.centered(graphics, font, authority, x + boxWidth / 2, y + boxHeight - 16,
                boxWidth - 24, UiTheme.TEXT_DIM);
    }

    private void drawProfile(GuiGraphicsExtractor graphics, UiLayout.Rect content) {
        int pad = 10;
        int identityHeight = UiLayout.clamp(content.height() * 27 / 100, 64, 100);
        UiLayout.Rect identity = new UiLayout.Rect(content.x() + pad, content.y() + pad,
                content.width() - 2 * pad, identityHeight);
        UiTheme.card(graphics, identity.x(), identity.y(), identity.width(), identity.height(),
                UiTheme.GOLD, 0.85F);
        int column = identity.width() / 4;
        drawIdentityField(graphics, identity, 0, column, "habilitesrandom.ability.character",
                ClientProfileCache.characterName(), UiTheme.TEXT);
        drawIdentityField(graphics, identity, 1, column, "habilitesrandom.ability.anime",
                ClientProfileCache.animeName(), UiTheme.TEXT_SOFT);
        drawIdentityField(graphics, identity, 2, column, "habilitesrandom.ability.rarity",
                ClientProfileCache.rarityName(), UiTheme.GOLD);
        drawIdentityField(graphics, identity, 3, column, "habilitesrandom.ability.category",
                ClientProfileCache.category(), UiTheme.PURPLE);

        int gridY = identity.bottom() + 8;
        int formHeight = 34;
        int gridHeight = Math.max(60, content.bottom() - gridY - formHeight - 18);
        List<UiLayout.Rect> columns = UiLayout.splitColumns(new UiLayout.Rect(content.x() + pad, gridY,
                content.width() - 2 * pad, gridHeight), 2, 8);
        for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
            UiLayout.Rect columnArea = columns.get(columnIndex);
            List<UiLayout.Rect> rows = UiLayout.splitRows(columnArea, 2, 8);
            for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
                int slot = columnIndex * 2 + rowIndex;
                drawAbilityCard(graphics, rows.get(rowIndex), slot);
            }
        }

        int formY = content.bottom() - formHeight - pad;
        UiTheme.card(graphics, content.x() + pad, formY, content.width() - 2 * pad, formHeight,
                UiTheme.PURPLE, 0.72F);
        UiText.keyValue(graphics, font,
                Component.translatable("habilitesrandom.ability.form").getString(),
                ClientProfileCache.transformationName().isBlank() ? "—" : ClientProfileCache.transformationName(),
                content.x() + pad + 8, formY + 6, 55 * (content.width() - 2 * pad) / 100,
                UiTheme.TEXT_DIM, UiTheme.TEXT);
        String uses = Component.translatable("habilitesrandom.ability.uses",
                ClientProfileCache.transformationUses()).getString();
        graphics.text(font, uses, content.right() - pad - 8 - font.width(uses), formY + 6,
                UiTheme.GOLD, false);
    }

    private void drawIdentityField(GuiGraphicsExtractor graphics, UiLayout.Rect identity, int index,
                                   int columnWidth, String labelKey, String value, int color) {
        int x = identity.x() + index * columnWidth + 8;
        int width = columnWidth - 14;
        UiText.label(graphics, font, Component.translatable(labelKey).getString(), x,
                identity.y() + 10, width, UiTheme.TEXT_DIM);
        UiText.label(graphics, font, value == null || value.isBlank() ? "—" : value, x,
                identity.y() + 26, width, color);
    }

    private AbilityDefinition displayedAbility(int slot) {
        var characterId = ProfileValues.parseId(ClientProfileCache.characterId());
        if (characterId == null) return null;
        CharacterDefinition character = CoreServices.characters().get(characterId).orElse(null);
        if (character == null || slot < 0 || slot >= character.abilityIds().size()) return null;
        return CoreServices.abilities().get(character.abilityIds().get(slot)).orElse(null);
    }

    private void drawAbilityCard(GuiGraphicsExtractor graphics, UiLayout.Rect rect, int slot) {
        UiTheme.card(graphics, rect.x(), rect.y(), rect.width(), rect.height(),
                slot % 2 == 0 ? UiTheme.CYAN : UiTheme.PURPLE, 0.75F);
        UiText.label(graphics, font, Component.translatable("habilitesrandom.ability.slot", slot + 1).getString(),
                rect.x() + 9, rect.y() + 7, rect.width() - 18, UiTheme.TEXT_DIM);
        AbilityDefinition ability = displayedAbility(slot);
        String name = ability == null ? "—" : ability.displayName();
        UiText.label(graphics, font, name, rect.x() + 9, rect.y() + 21, rect.width() - 18, UiTheme.TEXT);
        if (ability != null && rect.height() >= 52) {
            UiText.label(graphics, font, ability.description(), rect.x() + 9, rect.y() + 34,
                    rect.width() - 18, UiTheme.TEXT_SOFT);
            String cooldown = (ability.cooldownTicks() / 20) + "s";
            graphics.text(font, cooldown, rect.right() - 8 - font.width(cooldown), rect.bottom() - 23,
                    UiTheme.TEXT_DIM, false);
        }
        String key = ModKeyBindings.actionLabel(slot).getString();
        graphics.text(font, key, rect.right() - 8 - font.width(key), rect.y() + 7, UiTheme.GOLD, false);
        int remaining = ClientProfileCache.cooldownRemaining(slot);
        UiTheme.progress(graphics, rect.x() + 9, rect.bottom() - 10, rect.width() - 18, 3,
                remaining <= 0 ? 1.0F : Math.max(0.08F, remaining / 30.0F),
                remaining <= 0 ? UiTheme.GREEN : UiTheme.CYAN, 1.0F);
    }
}
