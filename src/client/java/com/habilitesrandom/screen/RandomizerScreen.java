package com.habilitesrandom.screen;

import com.habilitesrandom.client.ui.HubButton;
import com.habilitesrandom.client.ui.UiAnimation;
import com.habilitesrandom.client.ui.UiLayout;
import com.habilitesrandom.client.ui.UiText;
import com.habilitesrandom.client.ui.UiTheme;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

/** Cinematic result stage prepared for Prompt 3; no draw is performed or faked here. */
public final class RandomizerScreen extends AbstractHubScreen {
    private UiLayout.Rect portrait;
    private UiLayout.Rect details;

    public RandomizerScreen(Screen parent) {
        super(Component.translatable("habilitesrandom.page.randomizer"), parent);
    }

    @Override
    protected Component subtitle() {
        return Component.translatable("habilitesrandom.randomizer.preparing");
    }

    @Override
    protected void buildContent() {
        UiLayout.Rect content = content();
        boolean wide = content.width() >= 600 && content.height() >= 260;
        if (wide) {
            int portraitWidth = content.width() * 44 / 100;
            portrait = new UiLayout.Rect(content.x(), content.y(), portraitWidth, content.height());
            details = new UiLayout.Rect(portrait.right() + 12, content.y(),
                    content.width() - portraitWidth - 12, content.height());
        } else {
            int portraitHeight = UiLayout.clamp(content.height() * 43 / 100, 80, 180);
            portrait = new UiLayout.Rect(content.x(), content.y(), content.width(), portraitHeight);
            details = new UiLayout.Rect(content.x(), portrait.bottom() + 8, content.width(),
                    Math.max(60, content.bottom() - portrait.bottom() - 8));
        }

        UiLayout.Rect footer = layout().footer();
        List<UiLayout.Rect> actions = UiLayout.splitColumns(footer, 2, 8);
        addRenderableWidget(new HubButton(actions.get(0).x(), actions.get(0).y(),
                actions.get(0).width(), actions.get(0).height(),
                Component.translatable("habilitesrandom.randomizer.start"), Component.empty(),
                UiTheme.GOLD, false, button -> {
                }, Component.translatable("habilitesrandom.common.available_prompt", 3)));
        addRenderableWidget(HubButton.compact(actions.get(1).x(), actions.get(1).y(),
                actions.get(1).width(), actions.get(1).height(),
                Component.translatable("habilitesrandom.common.back"), UiTheme.CYAN, true, this::onClose, null));
    }

    @Override
    protected void drawContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (portrait == null || details == null) {
            return;
        }
        UiTheme.card(graphics, portrait.x(), portrait.y(), portrait.width(), portrait.height(),
                UiTheme.PURPLE, 0.86F);
        drawPortrait(graphics);
        UiTheme.card(graphics, details.x(), details.y(), details.width(), details.height(),
                UiTheme.CYAN, 0.64F);
        drawDetails(graphics);
    }

    private void drawPortrait(GuiGraphicsExtractor graphics) {
        long now = System.currentTimeMillis();
        int centerX = portrait.x() + portrait.width() / 2;
        int centerY = portrait.y() + portrait.height() / 2;
        int radius = Math.max(12, Math.min(portrait.width(), portrait.height()) / 3);

        for (int index = 0; index < 3; index++) {
            float phase = UiAnimation.pulse(now + index * 380L, 1800L);
            int color = index == 1 ? UiTheme.CYAN : UiTheme.PURPLE;
            int barWidth = (int) (radius * (0.8F + phase * 0.8F));
            graphics.fill(centerX - barWidth, centerY - radius - 8 - index * 8,
                    centerX + barWidth, centerY - radius - 7 - index * 8,
                    UiTheme.alpha(color, 0.18F + phase * 0.25F));
        }

        UiTheme.diamond(graphics, centerX, centerY, radius, 0x33182A50);
        UiTheme.diamond(graphics, centerX, centerY, Math.max(3, radius / 6),
                UiTheme.alpha(UiTheme.GOLD, 0.85F));
        UiText.centered(graphics, font, "?", centerX, centerY - font.lineHeight / 2,
                radius, UiTheme.GOLD);
        UiText.centered(graphics, font,
                Component.translatable("habilitesrandom.randomizer.preparing").getString(),
                centerX, portrait.bottom() - 16, portrait.width() - 12, UiTheme.TEXT_DIM);
    }

    private void drawDetails(GuiGraphicsExtractor graphics) {
        int x = details.x() + 12;
        int width = details.width() - 24;
        int y = details.y() + 10;
        UiText.label(graphics, font,
                Component.translatable("habilitesrandom.randomizer.description").getString(),
                x, y, width, UiTheme.TEXT_SOFT);
        int descriptionLines = font.split(Component.translatable("habilitesrandom.randomizer.description"),
                width).size();
        y += Math.min(4, descriptionLines) * (font.lineHeight + 2) + 8;
        UiTheme.divider(graphics, x, y, width, 0.8F);
        y += 8;

        String[] labels = {
                "habilitesrandom.randomizer.character",
                "habilitesrandom.randomizer.anime",
                "habilitesrandom.randomizer.category",
                "habilitesrandom.randomizer.rarity",
                "habilitesrandom.randomizer.chance"
        };
        int available = details.bottom() - y - 8;
        int rowHeight = available / labels.length;
        for (int index = 0; index < labels.length; index++) {
            int rowY = y + index * rowHeight;
            graphics.fill(x, rowY, x + width, rowY + Math.max(1, rowHeight - 3),
                    index % 2 == 0 ? 0x25152542 : 0x18101E32);
            UiText.keyValue(graphics, font, Component.translatable(labels[index]).getString(), "—",
                    x + 6, rowY + Math.max(1, (rowHeight - font.lineHeight - 3) / 2), width - 12,
                    UiTheme.TEXT_DIM, UiTheme.GOLD);
        }
    }
}
