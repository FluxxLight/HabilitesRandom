package com.habilitesrandom;

import com.habilitesrandom.data.PlayerDataLifecycle;
import com.habilitesrandom.network.ModNetworking;
import com.habilitesrandom.registry.AddonLoader;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Common/server entrypoint. The logical server always owns gameplay state. */
public final class HabilitesRandom implements ModInitializer {
    public static final String MOD_ID = "habilitesrandom";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModNetworking.register();
        PlayerDataLifecycle.register();
        AddonLoader.load();
        LOGGER.info("HabilitesRandom for Minecraft 26.3 initialized");
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
