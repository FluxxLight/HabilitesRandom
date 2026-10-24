package com.habilitesrandom.api;

import com.habilitesrandom.registry.*;
import com.habilitesrandom.api.player.PlayerApi;

/** Stable façade used by companion mods; the Core has no compile-time knowledge of any Plus. */
public interface HabilitesRandomApi {
    int apiVersion();

    ContentCatalog catalog();

    CharacterRegistry characters();
    AbilityRegistry abilities();
    TransformationRegistry transformations();
    PlayerApi players();

    boolean supports(int requestedApiVersion);
}
