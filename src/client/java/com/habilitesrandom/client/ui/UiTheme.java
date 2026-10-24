package com.habilitesrandom.client.ui;

import com.habilitesrandom.HabilitesRandom;
import com.habilitesrandom.config.ClientConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

/** One visual language for every HabilitesRandom screen and the in-game HUD. */
public final class UiTheme {
    public static final int TEXT = 0xFFF4F7FF;
    public static final int TEXT_SOFT = 0xFFC3CCE8;
    public static final int TEXT_DIM = 0xFF7E89AA;
    public static final int CYAN = 0xFF36D6FF;
    public static final int PURPLE = 0xFFA96BFF;
    public static final int GOLD = 0xFFFFC857;
    public static final int RED = 0xFFFF6B7D;
    public static final int GREEN = 0xFF62E6A8;
    public static final int BORDER = 0xFF43517D;
    public static final int PANEL_TOP = 0xF3131830;
    public static final int PANEL_BOTTOM = 0xF7070A15;
    public static final int CARD = 0xB4121830;
    public static final int CARD_DARK = 0xD8080C18;

    private static final Identifier ICON =
            Identifier.fromNamespaceAndPath(HabilitesRandom.MOD_ID, "textures/gui/icon.png");
    private static final int ICON_TEXTURE_SIZE = 256;
    private static final int AMBIENT_POINTS = 30;

    private UiTheme() {
    }

    public static int alpha(int color, float opacity) {
        int value = (int) (Math.max(0.0F, Math.min(1.0F, opacity)) * 255.0F);
        return (value << 24) | (color & 0x00FFFFFF);
    }

    public static void background(GuiGraphicsExtractor graphics, int width, int height, long now,
                                  ClientConfig config) {
        graphics.fillGradient(0, 0, width, height, 0xF0060915, 0xF0160A25);
        if (config != null && (!config.interfaceParticles || !config.visualEffects)) {
            return;
        }
        int intensity = config == null ? 1 : config.intensity().ordinal();
        int count = intensity == 0 ? AMBIENT_POINTS / 3 : intensity == 1 ? AMBIENT_POINTS * 2 / 3 : AMBIENT_POINTS;
        float motion = (float) (now % 20000L) / 45.0F;
        for (int index = 0; index < count; index++) {
            int seedX = (index * 97 + 31) % Math.max(1, width);
            int x = (seedX + (int) (Math.sin(index * 1.73 + motion * 0.018) * 18.0) + width) % Math.max(1, width);
            int seedY = (index * 61 + 17) % Math.max(1, height);
            int y = (seedY + (int) (motion * (0.35F + (index % 3) * 0.12F))) % Math.max(1, height);
            int size = index % 9 == 0 ? 2 : 1;
            int color = (index & 1) == 0 ? 0x3036D6FF : 0x30A96BFF;
            graphics.fill(x, y, x + size, y + size, color);
        }
        int beamX = (int) ((now / 60L) % Math.max(1, width + 240)) - 120;
        graphics.fill(beamX, 0, beamX + 1, height, 0x1236D6FF);
    }

    public static void panel(GuiGraphicsExtractor graphics, int x, int y, int width, int height, float opacity) {
        graphics.fill(x + 6, y + 7, x + width + 6, y + height + 7, alpha(0x000000, 0.45F * opacity));
        graphics.fillGradient(x, y, x + width, y + height, alpha(PANEL_TOP, opacity), alpha(PANEL_BOTTOM, opacity));
        graphics.fill(x, y, x + width, y + 1, alpha(BORDER, opacity));
        graphics.fill(x, y + height - 1, x + width, y + height, alpha(BORDER, opacity));
        graphics.fill(x, y, x + 1, y + height, alpha(BORDER, opacity));
        graphics.fill(x + width - 1, y, x + 1 + width - 1, y + height, alpha(BORDER, opacity));
        corners(graphics, x, y, width, height, alpha(CYAN, opacity));
    }

    public static void card(GuiGraphicsExtractor graphics, int x, int y, int width, int height,
                            int accent, float opacity) {
        graphics.fillGradient(x, y, x + width, y + height, alpha(CARD, opacity), alpha(CARD_DARK, opacity));
        graphics.fill(x, y, x + width, y + 1, alpha(BORDER, opacity));
        graphics.fill(x, y + height - 1, x + width, y + height, alpha(BORDER, opacity));
        graphics.fill(x, y, x + 1, y + height, alpha(BORDER, opacity));
        graphics.fill(x + width - 1, y, x + width, y + height, alpha(BORDER, opacity));
        graphics.fill(x + 1, y + 1, x + 5, y + height - 1, alpha(accent, 0.75F * opacity));
    }

    public static void corners(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color) {
        int length = Math.min(18, Math.min(width, height) / 4);
        graphics.fill(x, y, x + length, y + 2, color);
        graphics.fill(x, y, x + 2, y + length, color);
        graphics.fill(x + width - length, y, x + width, y + 2, color);
        graphics.fill(x + width - 2, y, x + width, y + length, color);
        graphics.fill(x, y + height - 2, x + length, y + height, color);
        graphics.fill(x, y + height - length, x + 2, y + height, color);
        graphics.fill(x + width - length, y + height - 2, x + width, y + height, color);
        graphics.fill(x + width - 2, y + height - length, x + width, y + height, color);
    }

    public static void divider(GuiGraphicsExtractor graphics, int x, int y, int width, float opacity) {
        graphics.fill(x, y, x + width, y + 1, alpha(BORDER, opacity));
        graphics.fill(x, y, x + Math.min(52, width / 3), y + 2, alpha(CYAN, opacity));
    }

    public static void progress(GuiGraphicsExtractor graphics, int x, int y, int width, int height,
                                float value, int color, float opacity) {
        graphics.fill(x, y, x + width, y + height, alpha(0x000000, 0.55F * opacity));
        int filled = (int) (Math.max(0.0F, Math.min(1.0F, value)) * width);
        graphics.fill(x, y, x + filled, y + height, alpha(color, opacity));
    }

    public static void icon(GuiGraphicsExtractor graphics, int x, int y, int size) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, ICON, x, y, 0, 0, size, size,
                size, size, ICON_TEXTURE_SIZE, ICON_TEXTURE_SIZE, -1);
    }

    public static void diamond(GuiGraphicsExtractor graphics, int centerX, int centerY, int radius, int color) {
        for (int row = -radius; row <= radius; row++) {
            int half = radius - Math.abs(row);
            graphics.fill(centerX - half, centerY + row, centerX + half + 1, centerY + row + 1, color);
        }
    }
}
