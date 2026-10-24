package com.habilitesrandom.screen;

import com.habilitesrandom.client.net.ClientProfileCache;
import com.habilitesrandom.client.ui.HubButton;
import com.habilitesrandom.client.ui.UiAnimation;
import com.habilitesrandom.client.ui.UiLayout;
import com.habilitesrandom.client.ui.UiText;
import com.habilitesrandom.client.ui.UiTheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import java.util.List;

/** Map and tracking presentation scaffold; no world structure is generated in Prompt 1. */
public final class SanctuaryScreen extends AbstractHubScreen {
    private UiLayout.Rect map;
    private UiLayout.Rect info;

    public SanctuaryScreen(Screen parent) {
        super(Component.translatable("habilitesrandom.page.sanctuary"), parent);
    }

    @Override
    protected Component subtitle() {
        return Component.translatable("habilitesrandom.sanctuary.discovered");
    }

    @Override
    protected void buildContent() {
        UiLayout.Rect content = content();
        boolean wide = content.width() >= 600 && content.height() >= 240;
        if (wide) {
            int mapWidth = content.width() * 58 / 100;
            map = new UiLayout.Rect(content.x(), content.y(), mapWidth, content.height());
            info = new UiLayout.Rect(map.right() + 10, content.y(),
                    content.width() - mapWidth - 10, content.height());
        } else {
            int mapHeight = UiLayout.clamp(content.height() * 55 / 100, 90, 220);
            map = new UiLayout.Rect(content.x(), content.y(), content.width(), mapHeight);
            info = new UiLayout.Rect(content.x(), map.bottom() + 8, content.width(),
                    Math.max(48, content.bottom() - map.bottom() - 8));
        }

        UiLayout.Rect footer = layout().footer();
        List<UiLayout.Rect> actions = UiLayout.splitColumns(footer, 2, 8);
        addRenderableWidget(new HubButton(actions.get(0).x(), actions.get(0).y(),
                actions.get(0).width(), actions.get(0).height(),
                Component.translatable("habilitesrandom.sanctuary.track"), Component.empty(), UiTheme.GREEN,
                false, button -> {
                }, Component.translatable("habilitesrandom.common.available_prompt", 4)));
        addRenderableWidget(HubButton.compact(actions.get(1).x(), actions.get(1).y(),
                actions.get(1).width(), actions.get(1).height(),
                Component.translatable("habilitesrandom.common.back"), UiTheme.CYAN, true, this::onClose, null));
    }

    @Override
    protected void drawContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (map == null || info == null) {
            return;
        }
        drawMap(graphics);
        drawInfo(graphics);
    }

    private void drawMap(GuiGraphicsExtractor graphics) {
        UiTheme.card(graphics, map.x(), map.y(), map.width(), map.height(), UiTheme.GREEN, 0.80F);
        int innerX = map.x() + 8;
        int innerY = map.y() + 8;
        int innerWidth = map.width() - 16;
        int innerHeight = map.height() - 16;
        graphics.fillGradient(innerX, innerY, innerX + innerWidth, innerY + innerHeight,
                0xB0071620, 0xB0100E28);

        int step = Math.max(18, innerWidth / 12);
        for (int x = innerX + step; x < innerX + innerWidth; x += step) {
            graphics.fill(x, innerY, x + 1, innerY + innerHeight, 0x2536D6FF);
        }
        for (int y = innerY + step; y < innerY + innerHeight; y += step) {
            graphics.fill(innerX, y, innerX + innerWidth, y + 1, 0x2536D6FF);
        }

        int centerX = innerX + innerWidth / 2;
        int centerY = innerY + innerHeight / 2;
        float pulse = UiAnimation.pulse(System.currentTimeMillis(), 2600L);
        UiTheme.diamond(graphics, centerX, centerY, 3, UiTheme.CYAN);
        UiTheme.diamond(graphics, centerX + Math.round(pulse * 10), centerY - 12, 2,
                UiTheme.alpha(UiTheme.GREEN, 0.35F + pulse * 0.55F));
        UiText.centered(graphics, font,
                Component.translatable("habilitesrandom.sanctuary.map").getString(),
                centerX, map.y() + 7, map.width() - 20, UiTheme.TEXT_DIM);
        UiText.centered(graphics, font,
                Component.translatable("habilitesrandom.sanctuary.signal", "—").getString(),
                centerX, map.bottom() - 16, map.width() - 20, UiTheme.GREEN);
    }

    private void drawInfo(GuiGraphicsExtractor graphics) {
        UiTheme.card(graphics, info.x(), info.y(), info.width(), info.height(), UiTheme.PURPLE, 0.65F);
        int x = info.x() + 10;
        int width = info.width() - 20;
        int y = info.y() + 8;
        UiText.wrapped(graphics, font, Component.translatable("habilitesrandom.sanctuary.description"),
                x, y, width, UiTheme.TEXT_SOFT, 3);
        y += UiText.wrappedHeight(font, Component.translatable("habilitesrandom.sanctuary.description"),
                width, 3) + 8;
        UiTheme.divider(graphics, x, y, width, 0.8F);
        y += 7;

        BlockPos playerPosition = Minecraft.getInstance().player == null
                ? BlockPos.ZERO : Minecraft.getInstance().player.blockPosition();
        String coordinates = playerPosition.getX() + " / " + playerPosition.getY() + " / " + playerPosition.getZ();
        UiText.keyValue(graphics, font,
                Component.translatable("habilitesrandom.sanctuary.location").getString(), "—",
                x, y, width, UiTheme.TEXT_DIM, UiTheme.TEXT);
        y += 15;
        UiText.keyValue(graphics, font,
                Component.translatable("habilitesrandom.sanctuary.coordinates").getString(), coordinates,
                x, y, width, UiTheme.TEXT_DIM, UiTheme.GOLD);
        y += 15;
        UiText.keyValue(graphics, font,
                Component.translatable("habilitesrandom.sanctuary.distance").getString(), "—",
                x, y, width, UiTheme.TEXT_DIM, UiTheme.TEXT);
        y += 15;
        UiText.keyValue(graphics, font,
                Component.translatable("habilitesrandom.sanctuary.state").getString(),
                Component.translatable("habilitesrandom.sanctuary.discovered").getString(),
                x, y, width, UiTheme.TEXT_DIM, UiTheme.TEXT_SOFT);

        int progressY = Math.min(info.bottom() - 18, y + 20);
        if (progressY > y + 16 && info.height() > 150) {
            UiTheme.progress(graphics, x, progressY, width, 4,
                    ClientProfileCache.sanctuaryProgress() / 100.0F, UiTheme.GREEN, 0.9F);
        }
    }
}
