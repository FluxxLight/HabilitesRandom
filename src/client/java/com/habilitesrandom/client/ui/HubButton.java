package com.habilitesrandom.client.ui;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

/** Accessible custom button: vanilla focus/keyboard/narration behavior with an original skin. */
public final class HubButton extends Button {
    private final int accent;
    private Component subtitle;
    private boolean selected;
    private boolean lastHovered;
    private long hoverChangedAt = System.currentTimeMillis();
    private long pressedAt = -1000L;

    public HubButton(int x, int y, int width, int height, Component message, Component subtitle,
                     int accent, boolean enabled, OnPress action, Component tooltip) {
        super(x, y, width, height, message, action, DEFAULT_NARRATION);
        this.subtitle = subtitle == null ? Component.empty() : subtitle;
        this.accent = accent;
        this.active = enabled;
        if (tooltip != null) {
            setTooltip(Tooltip.create(tooltip));
        }
    }

    public HubButton(int x, int y, int width, int height, Component message, Component subtitle,
                     int accent, boolean enabled, Runnable action) {
        this(x, y, width, height, message, subtitle, accent, enabled, button -> action.run(), null);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setSubtitle(Component subtitle) {
        this.subtitle = subtitle == null ? Component.empty() : subtitle;
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        pressedAt = System.currentTimeMillis();
        super.onClick(event, doubleClick);
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        long now = System.currentTimeMillis();
        boolean hovered = isHoveredOrFocused();
        if (hovered != lastHovered) {
            lastHovered = hovered;
            hoverChangedAt = now;
        }

        float hover = UiAnimation.easeOutCubic(UiAnimation.progress(hoverChangedAt, UiAnimation.HOVER_DURATION));
        if (!hovered) {
            hover = 1.0F - hover;
        }
        float press = 1.0F - UiAnimation.progress(pressedAt, 120L);
        float opacity = this.alpha * (active ? 1.0F : 0.42F);
        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();

        graphics.fill(x + 2, y + 3, x + width + 2, y + height + 3, UiTheme.alpha(0x000000, 0.45F * opacity));
        int top = UiTheme.alpha(active && hovered ? 0x26345C : 0x11182E, opacity);
        int bottom = UiTheme.alpha(0x080B16, opacity);
        graphics.fillGradient(x, y, x + width, y + height, top, bottom);

        int glowColor = selected ? UiTheme.GOLD : accent;
        int border = UiTheme.alpha(glowColor, opacity * (0.45F + 0.45F * hover + 0.10F * press));
        graphics.fill(x, y, x + width, y + 1, border);
        graphics.fill(x, y + height - 1, x + width, y + height, border);
        graphics.fill(x, y, x + 1, y + height, border);
        graphics.fill(x + width - 1, y, x + width, y + height, border);

        int stripeWidth = selected ? 4 : 2 + Math.round(2 * hover) + Math.round(press);
        graphics.fill(x + 1, y + 1, x + 1 + stripeWidth, y + height - 1,
                UiTheme.alpha(glowColor, opacity * (0.65F + 0.35F * hover)));
        if (hovered && active) {
            graphics.fill(x + width - 3 - Math.round(5 * hover), y + 2,
                    x + width - 2, y + height - 2, UiTheme.alpha(UiTheme.GOLD, opacity * 0.8F));
        }
        if (selected) {
            UiTheme.diamond(graphics, x + width - 9, y + height / 2, 2, UiTheme.alpha(UiTheme.GOLD, opacity));
        }

        var font = Minecraft.getInstance().font;
        boolean twoLines = height >= 34 && !subtitle.getString().isEmpty();
        int textX = x + 11 + stripeWidth;
        int maxWidth = width - 14 - stripeWidth;
        if (twoLines) {
            UiText.label(graphics, font, getMessage().getString(), textX, y + 5, maxWidth,
                    UiTheme.alpha(UiTheme.TEXT, opacity));
            UiText.label(graphics, font, subtitle.getString(), textX, y + height - 12, maxWidth,
                    UiTheme.alpha(UiTheme.TEXT_DIM, opacity));
        } else {
            int textY = y + (height - font.lineHeight) / 2;
            UiText.label(graphics, font, getMessage().getString(), textX, textY, maxWidth,
                    UiTheme.alpha(active ? UiTheme.TEXT : UiTheme.TEXT_DIM, opacity));
        }
    }

    public static HubButton compact(int x, int y, int width, int height, Component label, int accent,
                                    boolean enabled, Runnable action, Component tooltip) {
        return new HubButton(x, y, width, height, label, Component.empty(), accent, enabled, action, tooltip);
    }

    public static boolean isPrimaryMouse(MouseButtonEvent event) {
        return event.button() == InputConstants.MOUSE_BUTTON_LEFT;
    }
}
