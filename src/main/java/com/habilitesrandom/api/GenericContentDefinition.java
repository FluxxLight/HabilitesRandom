package com.habilitesrandom.api;

import net.minecraft.resources.Identifier;

/** Small typed extension record for addons that only need to announce extra catalog content. */
public record GenericContentDefinition(
        Identifier id,
        String sourceAddon,
        String type,
        String payload
) implements CatalogEntry {
    public GenericContentDefinition {
        if (id == null || sourceAddon == null || sourceAddon.isBlank()) {
            throw new IllegalArgumentException("Generic content requires an id and source addon");
        }
        type = type == null || type.isBlank() ? "generic" : type;
        payload = payload == null ? "" : payload;
    }
}
