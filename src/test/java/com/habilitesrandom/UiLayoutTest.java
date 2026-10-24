package com.habilitesrandom;

import com.habilitesrandom.client.ui.UiLayout;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UiLayoutTest {
    private static final int[][] RESOLUTIONS = {
            {1920, 1080}, {1600, 900}, {1366, 768}, {1024, 768}, {800, 600}, {640, 480}, {480, 360}
    };

    @Test
    void hubPanelsFitEveryTargetResolution() {
        for (int[] resolution : RESOLUTIONS) {
            UiLayout.HubLayout layout = UiLayout.hub(resolution[0], resolution[1]);
            assertTrue(layout.panel().width() <= resolution[0], "panel width at " + resolution[0]);
            assertTrue(layout.panel().height() <= resolution[1], "panel height at " + resolution[1]);
            assertFalse(layout.header().intersects(layout.hero()));
            assertFalse(layout.header().intersects(layout.menu()));
            assertFalse(layout.hero().intersects(layout.menu()));
        }
    }

    @Test
    void sixHubSlotsNeverOverlap() {
        for (int[] resolution : RESOLUTIONS) {
            UiLayout.HubLayout layout = UiLayout.hub(resolution[0], resolution[1]);
            List<UiLayout.Rect> slots = UiLayout.menuSlots(layout.menu(), 6);
            for (int first = 0; first < slots.size(); first++) {
                for (int second = first + 1; second < slots.size(); second++) {
                    assertFalse(slots.get(first).intersects(slots.get(second)),
                            "slot overlap at " + resolution[0] + "x" + resolution[1]);
                }
                assertTrue(slots.get(first).y() >= layout.menu().y());
                assertTrue(slots.get(first).bottom() <= layout.menu().bottom());
            }
        }
    }

    @Test
    void pageContentAndFooterStaySeparate() {
        for (int[] resolution : RESOLUTIONS) {
            UiLayout.PageLayout layout = UiLayout.page(resolution[0], resolution[1], 30);
            assertFalse(layout.header().intersects(layout.content()));
            assertFalse(layout.content().intersects(layout.footer()));
            assertTrue(layout.content().height() > 0);
        }
    }
}
