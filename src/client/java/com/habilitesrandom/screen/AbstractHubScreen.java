package com.habilitesrandom.screen;

import com.habilitesrandom.client.HabilitesRandomClient;
import com.habilitesrandom.client.ui.HubButton;
import com.habilitesrandom.client.ui.UiAnimation;
import com.habilitesrandom.client.ui.UiLayout;
import com.habilitesrandom.client.ui.UiText;
import com.habilitesrandom.client.ui.UiTheme;
import com.habilitesrandom.config.ClientConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Shared frame, entrance/exit animation, responsive geometry and navigation behavior. */
public abstract class AbstractHubScreen extends Screen {
    protected final Screen parent;
    private final long openedAt = System.currentTimeMillis();
    private UiLayout.PageLayout layout;
    private boolean closing;
    private long closingAt;

    protected AbstractHubScreen(Component title, Screen parent) {
        super(title);
        this.parent = parent;
    }

    protected int footerHeight() {
        return 30;
    }

    protected abstract Component subtitle();

    protected abstract void buildContent();

    @Override
    protected void init() {
        layout = UiLayout.page(this.width, this.height, footerHeight());
        buildContent();
    }

    protected UiLayout.PageLayout layout() {
        return layout;
    }

    protected UiLayout.Rect content() {
        return layout == null ? new UiLayout.Rect(0, 0, 0, 0) : layout.content();
    }

    protected void addFooterButton(Component label, Runnable action) {
        UiLayout.Rect footer = layout.footer();
        addRenderableWidget(HubButton.compact(footer.x(), footer.y(), footer.width(), footer.height(),
                label, UiTheme.CYAN, true, action, null));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        UiTheme.background(graphics, this.width, this.height, System.currentTimeMillis(),
                HabilitesRandomClient.config());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        drawFrame(graphics);
        drawContent(graphics, mouseX, mouseY, partialTick);
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }

    protected void drawContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
    }

    private void drawFrame(GuiGraphicsExtractor graphics) {
        if (layout == null) {
            return;
        }
        ClientConfig config = HabilitesRandomClient.config();
        float animation = config.interfaceAnimations
                ? UiAnimation.easeOutCubic(UiAnimation.progress(openedAt, UiAnimation.OPEN_DURATION)) : 1.0F;
        float closingFade = closing && config.interfaceAnimations
                ? 1.0F - UiAnimation.progress(closingAt, UiAnimation.CLOSE_DURATION) : closing ? 0.0F : 1.0F;
        float opacity = animation * closingFade;

        UiLayout.Rect panel = layout.panel();
        UiTheme.panel(graphics, panel.x(), panel.y(), panel.width(), panel.height(), opacity);
        drawHeader(graphics, opacity);
        fadeWidgets(opacity);
    }

    private void drawHeader(GuiGraphicsExtractor graphics, float opacity) {
        UiLayout.Rect header = layout.header();
        int iconSize = UiLayout.clamp(header.height() - 8, 22, 44);
        int iconX = header.x() + 2;
        int iconY = header.y() + Math.max(0, (header.height() - iconSize) / 2);
        UiTheme.icon(graphics, iconX, iconY, iconSize);

        int textX = iconX + iconSize + 10;
        int textWidth = Math.max(20, header.right() - textX);
        int titleY = header.y() + (subtitle().getString().isEmpty() ? (header.height() - font.lineHeight) / 2 : 7);
        UiText.label(graphics, font, getTitle().getString(), textX, titleY, textWidth,
                UiTheme.alpha(UiTheme.TEXT, opacity));
        if (!subtitle().getString().isEmpty() && header.height() >= 44) {
            UiText.label(graphics, font, subtitle().getString(), textX,
                    titleY + font.lineHeight + 3, textWidth, UiTheme.alpha(UiTheme.TEXT_DIM, opacity));
        }
        UiTheme.divider(graphics, header.x(), header.bottom() - 1, header.width(), opacity);
    }

    protected void navigate(Screen next) {
        onBeforeNavigate();
        if (this.minecraft != null) {
            this.minecraft.setScreenAndShow(next);
        }
    }

    protected void onBeforeNavigate() {
        onBeforeClose();
    }

    protected void onBeforeClose() {
        HabilitesRandomClient.saveConfig();
    }

    @Override
    public void onClose() {
        requestClose();
    }

    protected void requestClose() {
        if (closing) {
            return;
        }
        closing = true;
        closingAt = System.currentTimeMillis();
        onBeforeClose();
        if (!HabilitesRandomClient.config().interfaceAnimations) {
            finishClose();
        }
    }

    @Override
    public void tick() {
        if (closing && UiAnimation.progress(closingAt, UiAnimation.CLOSE_DURATION) >= 1.0F) {
            finishClose();
        }
    }

    private void finishClose() {
        if (this.minecraft == null) {
            return;
        }
        if (parent != null) {
            this.minecraft.setScreenAndShow(parent);
        } else {
            super.onClose();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
