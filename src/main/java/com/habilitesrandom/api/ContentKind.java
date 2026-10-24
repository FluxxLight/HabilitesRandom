package com.habilitesrandom.api;

import net.minecraft.resources.Identifier;

/**
 * Type token for a catalog bucket. Plus mods may define their own kinds, allowing content to grow
 * without changing the Core registry implementation.
 */
public record ContentKind<T extends CatalogEntry>(Identifier id, Class<T> type) {
    public ContentKind {
        if (id == null || type == null) {
            throw new IllegalArgumentException("Content kind requires an id and a type");
        }
    }
}
