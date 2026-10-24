package com.habilitesrandom.screen;

import com.habilitesrandom.client.net.ClientProfileCache;
import com.habilitesrandom.client.net.ClientNetworking;
import com.habilitesrandom.network.RandomizerResultPayload;
import com.habilitesrandom.client.ui.HubButton;
import com.habilitesrandom.client.ui.UiLayout;
import com.habilitesrandom.client.ui.UiText;
import com.habilitesrandom.client.ui.UiTheme;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

/** Visual state machine scaffold for the server-authoritative reroll system in Prompt 4. */
public final class RerollScreen extends AbstractHubScreen {
    private boolean confirming; private HubButton actionButton; private RandomizerResultPayload result;
    public RerollScreen(Screen parent) {
        super(Component.translatable("habilitesrandom.page.reroll"), parent);
    }

    @Override
    protected Component subtitle() {
        return Component.translatable("habilitesrandom.reroll.initial");
    }

    @Override
    protected void buildContent() {
        UiLayout.Rect footer = layout().footer();
        List<UiLayout.Rect> actions = UiLayout.splitColumns(footer, 2, 8);
        State current=State.from(ClientProfileCache.rerollState());
        actionButton = new HubButton(actions.get(0).x(), actions.get(0).y(), actions.get(0).width(), actions.get(0).height(),
                Component.translatable(confirming ? "habilitesrandom.reroll.confirm" : "habilitesrandom.reroll.action"), Component.empty(), UiTheme.GOLD, false, button -> {
                    if (!confirming) { confirming=true; refreshButtons(); } else { ClientNetworking.requestReroll(); actionButton.active=false; }
                }, null);
        actionButton.active = current == State.AVAILABLE || current == State.FINAL_AVAILABLE;
        addRenderableWidget(actionButton);
        addRenderableWidget(HubButton.compact(actions.get(1).x(), actions.get(1).y(),
                actions.get(1).width(), actions.get(1).height(),
                Component.translatable("habilitesrandom.common.back"), UiTheme.CYAN, true, this::onClose, null));
    }

    private void refreshButtons(){ clearWidgets(); buildContent(); }
    @Override public void tick(){super.tick();var incoming=ClientProfileCache.consumeRandomizerResult();if(incoming!=null){result=incoming;confirming=false;ClientNetworking.requestProfile();refreshButtons();}}

    @Override
    protected void drawContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        UiLayout.Rect content = content();
        int headlineHeight = UiLayout.clamp(content.height() * 30 / 100, 46, 130);
        UiLayout.Rect headline = new UiLayout.Rect(content.x(), content.y(), content.width(), headlineHeight);
        UiTheme.card(graphics, headline.x(), headline.y(), headline.width(), headline.height(),
                UiTheme.GOLD, 0.82F);

        State state = State.from(ClientProfileCache.rerollState());
        graphics.enableScissor(headline.x() + 2, headline.y() + 2,
                headline.right() - 2, headline.bottom() - 2);
        UiText.centered(graphics, font, Component.translatable(state.titleKey).getString(),
                headline.x() + headline.width() / 2, headline.y() + 18, headline.width() - 24,
                state.color);
        UiText.wrapped(graphics, font,
                Component.translatable(confirming ? "habilitesrandom.reroll.confirm.body" : headline.height() < 70
                        ? "habilitesrandom.reroll.description.short"
                        : "habilitesrandom.reroll.description"),
                headline.x() + 16, headline.y() + 32, headline.width() - 32,
                UiTheme.TEXT_SOFT, 2);
        graphics.disableScissor();

        List<UiLayout.Rect> cards = UiLayout.splitColumns(new UiLayout.Rect(content.x(),
                headline.bottom() + 10, content.width(),
                Math.max(52, content.bottom() - headline.bottom() - 10)), 3, 8);
        drawStateCard(graphics, cards.get(0), State.AVAILABLE, "01", UiTheme.GREEN);
        drawStateCard(graphics, cards.get(1), State.USED, "02", UiTheme.RED);
        drawStateCard(graphics, cards.get(2), State.SANCTUARY, "03", UiTheme.PURPLE);
    }

    private void drawStateCard(GuiGraphicsExtractor graphics, UiLayout.Rect rect, State cardState,
                               String number, int color) {
        State current = State.from(ClientProfileCache.rerollState());
        UiTheme.card(graphics, rect.x(), rect.y(), rect.width(), rect.height(), color,
                cardState == current ? 0.92F : 0.45F);
        graphics.enableScissor(rect.x() + 2, rect.y() + 2, rect.right() - 2, rect.bottom() - 2);
        UiText.label(graphics, font, number, rect.x() + 8, rect.y() + 7, rect.width() - 16, color);
        boolean compact = rect.height() < 110;
        Component label = compact && cardState == State.SANCTUARY
                ? Component.translatable("habilitesrandom.reroll.sanctuary.short")
                : Component.translatable(cardState.titleKey);
        UiText.wrapped(graphics, font, label,
                rect.x() + 8, rect.y() + 24, rect.width() - 16,
                cardState == current ? UiTheme.TEXT : UiTheme.TEXT_DIM, compact ? 2 : 3);
        graphics.disableScissor();
        if (cardState == current) {
            graphics.fill(rect.x() + 1, rect.bottom() - 3, rect.right() - 1, rect.bottom() - 1,
                    UiTheme.alpha(color, 0.85F));
        }
    }

    private enum State {
        INITIAL("habilitesrandom.reroll.initial", UiTheme.CYAN),
        AVAILABLE("habilitesrandom.reroll.available", UiTheme.GREEN),
        USED("habilitesrandom.reroll.used", UiTheme.RED),
        SANCTUARY("habilitesrandom.reroll.sanctuary", UiTheme.PURPLE),
        FINAL_AVAILABLE("habilitesrandom.reroll.final.available", UiTheme.GOLD),
        EXHAUSTED("habilitesrandom.reroll.exhausted", UiTheme.RED);

        private final String titleKey;
        private final int color;

        State(String titleKey, int color) {
            this.titleKey = titleKey;
            this.color = color;
        }

        private static State from(String wireState) {
            if (wireState == null) {
                return INITIAL;
            }
            return switch (wireState) {
                case "FREE_AVAILABLE" -> AVAILABLE;
                case "SANCTUARY_REQUIRED" -> SANCTUARY;
                case "SANCTUARY_AVAILABLE" -> FINAL_AVAILABLE;
                case "EXHAUSTED" -> EXHAUSTED;
                default -> INITIAL;
            };
        }
    }
}
