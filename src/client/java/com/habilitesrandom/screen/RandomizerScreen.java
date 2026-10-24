package com.habilitesrandom.screen;

import com.habilitesrandom.client.ui.HubButton;
import com.habilitesrandom.client.net.ClientNetworking;
import com.habilitesrandom.client.net.ClientProfileCache;
import com.habilitesrandom.network.RandomizerResultPayload;
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
    private HubButton startButton;
    private State state = State.IDLE;
    private long stateAt;
    private RandomizerResultPayload result;
    private enum State { IDLE, ROLLING, REVEAL, DENIED }

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
        boolean wide = content.width() >= 600 && content.height() >= 220;
        if (wide) {
            int portraitWidth = content.width() * 44 / 100;
            portrait = new UiLayout.Rect(content.x(), content.y(), portraitWidth, content.height());
            details = new UiLayout.Rect(portrait.right() + 12, content.y(),
                    content.width() - portraitWidth - 12, content.height());
        } else {
            int portraitHeight = UiLayout.clamp(content.height() * 40 / 100, 36,
                    Math.min(170, content.height()));
            portrait = new UiLayout.Rect(content.x(), content.y(), content.width(), portraitHeight);
            details = new UiLayout.Rect(content.x(), portrait.bottom() + 6, content.width(),
                    Math.max(28, content.bottom() - portrait.bottom() - 6));
        }

        UiLayout.Rect footer = layout().footer();
        List<UiLayout.Rect> actions = UiLayout.splitColumns(footer, 2, 8);
        startButton = new HubButton(actions.get(0).x(), actions.get(0).y(),
                actions.get(0).width(), actions.get(0).height(),
                Component.translatable(ClientProfileCache.hasCharacter() ? "habilitesrandom.randomizer.owned" : "habilitesrandom.randomizer.roll"), Component.empty(),
                UiTheme.GOLD, false, button -> beginRoll(), null);
        startButton.active = !ClientProfileCache.hasCharacter() && state == State.IDLE;
        addRenderableWidget(startButton);
        addRenderableWidget(HubButton.compact(actions.get(1).x(), actions.get(1).y(),
                actions.get(1).width(), actions.get(1).height(),
                Component.translatable("habilitesrandom.common.back"), UiTheme.CYAN, true, this::onClose, null));
    }

    private void beginRoll() {
        if (state != State.IDLE || ClientProfileCache.hasCharacter()) return;
        state = State.ROLLING; stateAt = System.currentTimeMillis(); startButton.active = false;
        ClientNetworking.requestRandomizer();
    }

    @Override public void tick() {
        super.tick();
        RandomizerResultPayload incoming = ClientProfileCache.consumeRandomizerResult();
        if (incoming != null) { result=incoming; state=incoming.success()?State.REVEAL:State.DENIED; stateAt=System.currentTimeMillis(); ClientNetworking.requestProfile(); }
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
        graphics.enableScissor(portrait.x() + 2, portrait.y() + 2,
                portrait.right() - 2, portrait.bottom() - 2);
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
        UiText.centered(graphics, font, state == State.REVEAL && result != null ? result.characterName() : state == State.ROLLING ? rollingName(now) : "?", centerX, centerY - font.lineHeight / 2,
                Math.max(radius * 2, portrait.width() - 20), state == State.REVEAL && result != null ? result.rarityColor() : UiTheme.GOLD);
        UiText.centered(graphics, font,
                (state == State.REVEAL ? Component.translatable("habilitesrandom.randomizer.received").getString() : state == State.ROLLING ? Component.translatable("habilitesrandom.randomizer.rolling").getString() : Component.translatable("habilitesrandom.randomizer.preparing").getString()),
                centerX, portrait.bottom() - 14, portrait.width() - 12, UiTheme.TEXT_DIM);
        graphics.disableScissor();
    }

    private void drawDetails(GuiGraphicsExtractor graphics) {
        int x = details.x() + 12;
        int width = details.width() - 24;
        int y = details.y() + 10;
        boolean compact = details.height() < 120;
        Component description = Component.translatable(compact
                ? "habilitesrandom.randomizer.description.short"
                : "habilitesrandom.randomizer.description");
        int descriptionLimit = compact ? 2
                : Math.max(1, Math.min(4, (details.height() - 50) / (font.lineHeight + 2)));

        graphics.enableScissor(details.x() + 2, details.y() + 2, details.right() - 2, details.bottom() - 2);
        int descriptionHeight = UiText.wrapped(graphics, font, description, x, y, width,
                UiTheme.TEXT_SOFT, descriptionLimit);
        y += descriptionHeight + 8;
        if (y < details.bottom() - 10) {
            UiTheme.divider(graphics, x, y, width, 0.8F);
            y += 8;
        }

        String[] labels = {
                "habilitesrandom.randomizer.character",
                "habilitesrandom.randomizer.anime",
                "habilitesrandom.randomizer.category",
                "habilitesrandom.randomizer.rarity",
                "habilitesrandom.randomizer.chance"
        };
        int available = Math.max(0, details.bottom() - y - 8);
        int rowHeight = Math.max(1, available / labels.length);
        for (int index = 0; index < labels.length && y + rowHeight <= details.bottom() - 4; index++) {
            int rowY = y + index * rowHeight;
            graphics.fill(x, rowY, x + width, rowY + Math.max(1, rowHeight - 2),
                    index % 2 == 0 ? 0x25152542 : 0x18101E32);
            String value = detailValue(index);
            UiText.keyValue(graphics, font, Component.translatable(labels[index]).getString(), value,
                    x + 6, rowY + Math.max(0, (rowHeight - font.lineHeight - 2) / 2), width - 12,
                    UiTheme.TEXT_DIM, UiTheme.GOLD);
        }
        graphics.disableScissor();
    }
    private String rollingName(long now) {
        var all = com.habilitesrandom.core.CoreServices.characters().getAll();
        if (all.isEmpty()) return "…";
        return all.get((int)((now / 90L) % all.size())).displayName();
    }
    private String detailValue(int index) {
        if (result == null || !result.success()) return state == State.DENIED && index == 0 ? Component.translatable("habilitesrandom.randomizer.denied." + result.reason()).getString() : "—";
        return switch(index){case 0->result.characterName();case 1->result.animeName();case 2->result.category();case 3->result.rarityName();case 4->Component.translatable("habilitesrandom.randomizer.server_decided").getString();default->"—";};
    }

}
