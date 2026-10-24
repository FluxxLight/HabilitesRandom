package com.habilitesrandom.client.ui;

import java.util.ArrayList;
import java.util.List;

/** Pure responsive geometry, unit-tested independently from Minecraft rendering. */
public final class UiLayout {
    private UiLayout() {
    }

    public record Rect(int x, int y, int width, int height) {
        public Rect {
            width = Math.max(0, width);
            height = Math.max(0, height);
        }

        public int right() {
            return x + width;
        }

        public int bottom() {
            return y + height;
        }

        public boolean intersects(Rect other) {
            return other != null && x < other.right() && right() > other.x
                    && y < other.bottom() && bottom() > other.y;
        }

        public Rect inset(int amount) {
            return new Rect(x + amount, y + amount,
                    Math.max(0, width - 2 * amount), Math.max(0, height - 2 * amount));
        }
    }

    public record HubLayout(Rect panel, Rect header, Rect hero, Rect menu, boolean wide) {
    }

    public record PageLayout(Rect panel, Rect header, Rect content, Rect footer) {
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static Rect panel(int screenWidth, int screenHeight) {
        int margin = clamp(Math.min(screenWidth, screenHeight) / 24, 8, 28);
        int width = Math.max(240, Math.min(screenWidth - 2 * margin, 1040));
        int height = Math.max(180, Math.min(screenHeight - 2 * margin, 680));
        return new Rect((screenWidth - width) / 2, (screenHeight - height) / 2, width, height);
    }

    public static HubLayout hub(int screenWidth, int screenHeight) {
        Rect panel = panel(screenWidth, screenHeight);
        int headerHeight = clamp(panel.height() * 11 / 100, 44, 72);
        int pad = clamp(panel.width() / 70, 10, 18);
        Rect header = new Rect(panel.x() + pad, panel.y() + 8, panel.width() - 2 * pad, headerHeight);
        int bodyY = header.bottom() + 4;
        int bodyHeight = Math.max(80, panel.bottom() - bodyY - pad);
        boolean wide = panel.width() >= 720 && bodyHeight >= 300;

        if (wide) {
            int gap = 16;
            int heroWidth = (panel.width() - pad * 2 - gap) * 42 / 100;
            int menuWidth = panel.width() - pad * 2 - gap - heroWidth;
            Rect hero = new Rect(panel.x() + pad, bodyY, heroWidth, bodyHeight);
            Rect menu = new Rect(hero.right() + gap, bodyY, menuWidth, bodyHeight);
            return new HubLayout(panel, header, hero, menu, true);
        }

        int heroHeight = clamp(bodyHeight * 24 / 100, 42, 130);
        Rect hero = new Rect(panel.x() + pad, bodyY, panel.width() - 2 * pad, heroHeight);
        Rect menu = new Rect(panel.x() + pad, hero.bottom() + 6,
                panel.width() - 2 * pad, Math.max(50, panel.bottom() - pad - hero.bottom() - 6));
        return new HubLayout(panel, header, hero, menu, false);
    }

    public static PageLayout page(int screenWidth, int screenHeight, int footerHeight) {
        Rect panel = panel(screenWidth, screenHeight);
        int pad = clamp(panel.width() / 70, 10, 18);
        int headerHeight = clamp(panel.height() * 11 / 100, 44, 68);
        int safeFooter = clamp(footerHeight, 22, Math.max(24, panel.height() / 5));
        Rect header = new Rect(panel.x() + pad, panel.y() + 8, panel.width() - 2 * pad, headerHeight);
        Rect footer = new Rect(panel.x() + pad, panel.bottom() - pad - safeFooter,
                panel.width() - 2 * pad, safeFooter);
        int contentY = header.bottom() + 4;
        int contentHeight = Math.max(36, footer.y() - contentY - 18);
        Rect content = new Rect(panel.x() + pad, contentY, panel.width() - 2 * pad, contentHeight);
        return new PageLayout(panel, header, content, footer);
    }

    /** Even grid used by the HUB menu. The six entries never overlap at supported resolutions. */
    public static List<Rect> menuSlots(Rect area, int count) {
        List<Rect> slots = new ArrayList<>(count);
        if (area == null || count <= 0 || area.width() <= 0 || area.height() <= 0) {
            return slots;
        }

        int columns = count >= 5 && area.width() >= 260 ? 2 : 1;
        int rows = (count + columns - 1) / columns;
        int gap = area.height() < 170 ? 5 : 8;
        int availableHeight = area.height() - gap * (rows - 1);
        int buttonHeight = clamp(availableHeight / Math.max(1, rows), 18, 48);
        int usedHeight = buttonHeight * rows + gap * (rows - 1);
        int startY = area.y() + Math.max(0, (area.height() - usedHeight) / 2);
        int columnWidth = (area.width() - gap * (columns - 1)) / columns;

        for (int index = 0; index < count; index++) {
            int column = index % columns;
            int row = index / columns;
            int x = area.x() + column * (columnWidth + gap);
            int width = column == columns - 1 ? area.right() - x : columnWidth;
            slots.add(new Rect(x, startY + row * (buttonHeight + gap), width, buttonHeight));
        }
        return slots;
    }

    public static List<Rect> splitRows(Rect area, int count, int gap) {
        List<Rect> rows = new ArrayList<>(count);
        if (area == null || count <= 0) {
            return rows;
        }
        int safeGap = Math.max(0, gap);
        int rowHeight = Math.max(1, (area.height() - safeGap * (count - 1)) / count);
        for (int index = 0; index < count; index++) {
            rows.add(new Rect(area.x(), area.y() + index * (rowHeight + safeGap), area.width(), rowHeight));
        }
        return rows;
    }

    public static List<Rect> splitColumns(Rect area, int count, int gap) {
        List<Rect> columns = new ArrayList<>(count);
        if (area == null || count <= 0) {
            return columns;
        }
        int safeGap = Math.max(0, gap);
        int columnWidth = Math.max(1, (area.width() - safeGap * (count - 1)) / count);
        for (int index = 0; index < count; index++) {
            int x = area.x() + index * (columnWidth + safeGap);
            int width = index == count - 1 ? area.right() - x : columnWidth;
            columns.add(new Rect(x, area.y(), width, area.height()));
        }
        return columns;
    }
}
