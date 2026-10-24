package com.habilitesrandom.registry;

import com.habilitesrandom.HabilitesRandom;
import com.habilitesrandom.api.HabilitesRandomAddon;
import com.habilitesrandom.core.CoreServices;
import net.fabricmc.loader.api.FabricLoader;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Loads external Plus registrations through Fabric's named entrypoint mechanism. */
public final class AddonLoader {
    public static final String ENTRYPOINT_KEY = "habilitesrandom:addon";

    private AddonLoader() {
    }

    public static AddonReport load() {
        List<HabilitesRandomAddon> addons = new ArrayList<>(
                FabricLoader.getInstance().getEntrypoints(ENTRYPOINT_KEY, HabilitesRandomAddon.class));
        addons.sort(Comparator.comparing(HabilitesRandomAddon::addonId));

        int loaded = 0;
        int failed = 0;
        for (HabilitesRandomAddon addon : addons) {
            try {
                int requested = addon.requiredApiVersion();
                if (!CoreServices.supports(requested)) {
                    failed++;
                    HabilitesRandom.LOGGER.error("Addon '{}' requires Core API {} but {} is installed",
                            addon.addonId(), requested, CoreServices.API_VERSION);
                    continue;
                }
                addon.register(new DefaultContentRegistrar(CoreServices.catalog(), addon.addonId(),
                        CoreServices.API_VERSION));
                loaded++;
            } catch (Throwable error) {
                failed++;
                // One broken addon must never prevent the Core from starting.
                HabilitesRandom.LOGGER.error("Failed to register HabilitesRandom addon '{}'", addon.addonId(), error);
            }
        }

        HabilitesRandom.LOGGER.info("HabilitesRandom Core API v{} ready: {} addon(s), {} failed, {} catalog entries",
                CoreServices.API_VERSION, loaded, failed, CoreServices.catalog().totalSize());
        return new AddonReport(loaded, failed);
    }

    public record AddonReport(int loaded, int failed) {
    }
}
