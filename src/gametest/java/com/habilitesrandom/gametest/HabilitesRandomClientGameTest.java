package com.habilitesrandom.gametest;

import com.habilitesrandom.client.net.ClientNetworking;
import com.habilitesrandom.client.net.ClientProfileCache;
import com.habilitesrandom.screen.AbilityScreen;
import com.habilitesrandom.screen.HubScreen;
import com.habilitesrandom.screen.RandomizerScreen;
import com.habilitesrandom.screen.RerollScreen;
import com.habilitesrandom.screen.SanctuaryScreen;
import com.habilitesrandom.screen.SettingsScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.screenshot.TestScreenshotOptions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

/** Real client boot: automatic HUB, key reopen, every initial page, server snapshot and clean leave. */
public final class HabilitesRandomClientGameTest implements FabricClientGameTest {
    @Override
    public void runTest(ClientGameTestContext context) {
        Path shots = shotsDir();
        try {
            Files.createDirectories(shots);
        } catch (IOException error) {
            throw new UncheckedIOException(error);
        }

        var world = context.worldBuilder().create();
        context.waitForScreen(HubScreen.class);
        context.waitFor(mc -> ClientProfileCache.isLinked(), 200);
        check(context.computeOnClient(mc -> ClientProfileCache.isLinked()),
                "the integrated server should send an authoritative profile snapshot");
        check(context.computeOnClient(mc -> ClientNetworking.serverHasMod()),
                "serverbound channels should be negotiated on an integrated server");
        context.waitTicks(5);
        context.takeScreenshot(shot("01-hub-auto-open", shots));

        closeScreen(context);
        context.getInput().pressKey(InputConstants.KEY_H);
        context.waitForScreen(HubScreen.class);
        context.takeScreenshot(shot("02-hub-key-open", shots));
        closeScreen(context);

        showScreen(context, shots, "03-randomizer", RandomizerScreen.class, () -> new RandomizerScreen(null));
        showScreen(context, shots, "04-ability", AbilityScreen.class, () -> new AbilityScreen(null));
        showScreen(context, shots, "05-reroll", RerollScreen.class, () -> new RerollScreen(null));
        showScreen(context, shots, "06-sanctuary", SanctuaryScreen.class, () -> new SanctuaryScreen(null));
        showScreen(context, shots, "07-settings", SettingsScreen.class, () -> new SettingsScreen(null));

        world.close();
        context.waitFor(mc -> !ClientProfileCache.isLinked(), 100);
        check(context.computeOnClient(mc -> mc.gui.screen() == null),
                "leaving the world must not leave a HabilitesRandom screen behind");
    }

    private static <T extends Screen> void showScreen(ClientGameTestContext context, Path shots, String name,
                                                       Class<T> screenClass, Supplier<T> factory) {
        context.runOnClient(mc -> mc.setScreenAndShow(factory.get()));
        context.waitForScreen(screenClass);
        context.waitTicks(5);
        context.takeScreenshot(shot(name, shots));
        closeScreen(context);
    }

    private static void closeScreen(ClientGameTestContext context) {
        context.getInput().pressKey(InputConstants.KEY_ESCAPE);
        context.waitFor(mc -> mc.gui.screen() == null);
    }

    private static TestScreenshotOptions shot(String name, Path directory) {
        return TestScreenshotOptions.of(name).disableCounterPrefix().withDestinationDir(directory);
    }

    private static Path shotsDir() {
        return FabricLoader.getInstance().getGameDir().resolve("../../gametest").normalize();
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
