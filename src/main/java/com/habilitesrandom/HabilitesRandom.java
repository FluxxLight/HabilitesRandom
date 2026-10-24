package com.habilitesrandom;

import com.habilitesrandom.data.PlayerDataLifecycle;
import com.habilitesrandom.content.BuiltinContent;
import com.habilitesrandom.core.CoreServices;
import com.habilitesrandom.registry.DefaultContentRegistrar;
import com.habilitesrandom.registry.CatalogValidator;
import com.habilitesrandom.config.server.ServerConfigStore;
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
        ServerConfigStore.reload();
        ModNetworking.register();
        PlayerDataLifecycle.register();
        BuiltinContent.register(new DefaultContentRegistrar(CoreServices.catalog(), MOD_ID, CoreServices.API_VERSION));
        AddonLoader.load();
        CatalogValidator.validate(CoreServices.catalog());
        LOGGER.info("HabilitesRandom for Minecraft 26.3 initialized");
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
