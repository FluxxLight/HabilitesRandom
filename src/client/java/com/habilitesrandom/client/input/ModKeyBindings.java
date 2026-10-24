package com.habilitesrandom.client.input;

import com.habilitesrandom.HabilitesRandom;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

/** One global action set for every future character, remappable in the HUB settings. */
public final class ModKeyBindings {
    private static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(HabilitesRandom.id("main"));

    public static final KeyMapping HUB = register("key.habilitesrandom.hub", InputConstants.KEY_H);
    public static final KeyMapping ABILITY_1 = register("key.habilitesrandom.ability_1", InputConstants.KEY_Z);
    public static final KeyMapping ABILITY_2 = register("key.habilitesrandom.ability_2", InputConstants.KEY_X);
    public static final KeyMapping ABILITY_3 = register("key.habilitesrandom.ability_3", InputConstants.KEY_C);
    public static final KeyMapping ABILITY_4 = register("key.habilitesrandom.ability_4", InputConstants.KEY_V);
    public static final KeyMapping POWERFUL_FORM = register("key.habilitesrandom.powerful_form",
            InputConstants.KEY_LALT);

    private static final KeyMapping[] GLOBAL_ACTIONS =
            {ABILITY_1, ABILITY_2, ABILITY_3, ABILITY_4, POWERFUL_FORM};

    private ModKeyBindings() {
    }

    public static void register() {
        // Static fields are registered above; this method documents the explicit initialization order.
    }

    private static KeyMapping register(String name, int defaultKey) {
        return KeyMappingHelper.registerKeyMapping(new KeyMapping(name, defaultKey, CATEGORY));
    }

    public static KeyMapping[] globalActions() {
        return GLOBAL_ACTIONS.clone();
    }

    public static KeyMapping globalAction(int slot) {
        return GLOBAL_ACTIONS[Math.max(0, Math.min(GLOBAL_ACTIONS.length - 1, slot))];
    }

    public static void resetDefaults() {
        for (KeyMapping mapping : GLOBAL_ACTIONS) {
            mapping.setKey(mapping.getDefaultKey());
        }
        KeyMapping.resetMapping();
        Minecraft.getInstance().options.save();
    }

    public static void applyRemap(KeyMapping mapping, InputConstants.Key key) {
        mapping.setKey(key);
        KeyMapping.resetMapping();
        Minecraft.getInstance().options.save();
    }

    public static String hubLabel() {
        return HUB.getTranslatedKeyMessage().getString();
    }

    public static Component actionLabel(int slot) {
        return globalAction(slot).getTranslatedKeyMessage();
    }
}
