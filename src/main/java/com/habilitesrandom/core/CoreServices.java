package com.habilitesrandom.core;

import com.habilitesrandom.api.HabilitesRandomApi;
import com.habilitesrandom.registry.*;

/**
 * Single Core composition root. Companion mods talk to {@link #api()}; nothing here imports a Plus.
 */
public final class CoreServices {
    public static final int API_VERSION = 1;
    public static final int MINIMUM_COMPATIBLE_API = 1;

    private static final ContentCatalog CATALOG = new ContentCatalog();
    private static final HabilitesRandomApi API = new CoreApi();
    private static final CharacterRegistry CHARACTERS = new CharacterRegistry(CATALOG);
    private static final AbilityRegistry ABILITIES = new AbilityRegistry(CATALOG);
    private static final TransformationRegistry TRANSFORMATIONS = new TransformationRegistry(CATALOG);

    private CoreServices() {
    }

    public static ContentCatalog catalog() {
        return CATALOG;
    }

    public static CharacterRegistry characters() { return CHARACTERS; }
    public static AbilityRegistry abilities() { return ABILITIES; }
    public static TransformationRegistry transformations() { return TRANSFORMATIONS; }

    public static HabilitesRandomApi api() {
        return API;
    }

    public static boolean supports(int requestedApiVersion) {
        return requestedApiVersion >= MINIMUM_COMPATIBLE_API && requestedApiVersion <= API_VERSION;
    }

    private record CoreApi() implements HabilitesRandomApi {
        @Override
        public int apiVersion() {
            return API_VERSION;
        }

        @Override
        public ContentCatalog catalog() {
            return CATALOG;
        }

        @Override
        public boolean supports(int requestedApiVersion) {
            return CoreServices.supports(requestedApiVersion);
        }
    }
}
