package com.habilitesrandom.api;

import com.habilitesrandom.registry.ContentCatalog;

/** Stable façade used by companion mods; the Core has no compile-time knowledge of any Plus. */
public interface HabilitesRandomApi {
    int apiVersion();

    ContentCatalog catalog();

    boolean supports(int requestedApiVersion);
}
