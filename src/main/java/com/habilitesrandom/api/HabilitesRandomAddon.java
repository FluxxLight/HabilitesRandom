package com.habilitesrandom.api;

/** Fabric entrypoint contract exposed under {@code habilitesrandom:addon}. */
public interface HabilitesRandomAddon {
    String addonId();

    default int requiredApiVersion() {
        return 1;
    }

    void register(HabilitesRandomRegistrar registrar);
}
