package com.habilitesrandom.api;

import net.minecraft.resources.Identifier;

/** Every object stored in the shared catalog has a stable, namespaced id. */
public interface CatalogEntry {
    Identifier id();
}
