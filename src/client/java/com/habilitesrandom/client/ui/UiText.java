package com.habilitesrandom.client.ui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

/** Text measurement and clipping shared by every page, preventing labels from escaping cards. */
public final class UiText {
    private static final String ELLIPSIS = "…";

    private UiText() {
    }

    public static String ellipsize(Font font, String value, int maxWidth) {
        String safe = value == null ? "" : value;
        if (maxWidth <= 0 || font.width(safe) <= maxWidth) {
            return safe;
        }
        int ellipsisWidth = font.width(ELLIPSIS);
        if (maxWidth <= ellipsisWidth) {
            return font.plainSubstrByWidth(ELLIPSIS, maxWidth);
        }
        return font.plainSubstrByWidth(safe, maxWidth - ellipsisWidth) + ELLIPSIS;
    }

    public static void label(GuiGraphicsExtractor graphics, Font font, String value, int x, int y,
                             int maxWidth, int color) {
        graphics.text(font, ellipsize(font, value, maxWidth), x, y, color, false);
    }

    public static void centered(GuiGraphicsExtractor graphics, Font font, String value, int centerX, int y,
                                int maxWidth, int color) {
        String clipped = ellipsize(font, value, maxWidth);
        graphics.text(font, clipped, centerX - font.width(clipped) / 2, y, color, false);
    }

    public static int wrapped(GuiGraphicsExtractor graphics, Font font, Component value, int x, int y,
                              int maxWidth, int color, int maxLines) {
        if (maxWidth <= 0 || maxLines <= 0) {
            return 0;
        }
        List<FormattedCharSequence> lines = font.split(value, maxWidth);
        int drawn = Math.min(maxLines, lines.size());
        for (int index = 0; index < drawn; index++) {
            graphics.text(font, lines.get(index), x, y + index * (font.lineHeight + 2), color, false);
        }
        return drawn * (font.lineHeight + 2);
    }

    public static int wrappedHeight(Font font, Component value, int maxWidth, int maxLines) {
        if (maxWidth <= 0 || maxLines <= 0) {
            return 0;
        }
        return Math.min(maxLines, font.split(value, maxWidth).size()) * (font.lineHeight + 2);
    }

    public static void keyValue(GuiGraphicsExtractor graphics, Font font, String key, String value,
                                int x, int y, int width, int keyColor, int valueColor) {
        int half = Math.max(30, width * 40 / 100);
        label(graphics, font, key, x, y, half - 6, keyColor);
        String clipped = ellipsize(font, value, width - half - 4);
        graphics.text(font, clipped, x + width - font.width(clipped), y, valueColor, false);
    }
}
