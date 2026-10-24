package com.habilitesrandom;

import com.habilitesrandom.api.AnimeDefinition;
import com.habilitesrandom.api.ContentTypes;
import com.habilitesrandom.registry.ContentCatalog;
import net.minecraft.resources.Identifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContentCatalogTest {
    @Test
    void duplicateIdsAreRejectedInsteadOfOverwritten() {
        ContentCatalog catalog = new ContentCatalog();
        Identifier id = Identifier.fromNamespaceAndPath("plus-a", "show");
        assertTrue(catalog.register(ContentTypes.ANIME, new AnimeDefinition(id, "First", ""), "plus-a"));
        assertFalse(catalog.register(ContentTypes.ANIME, new AnimeDefinition(id, "Second", ""), "plus-b"));

        assertEquals("First", catalog.find(ContentTypes.ANIME, id).orElseThrow().displayName());
        assertEquals(1, catalog.size(ContentTypes.ANIME));
        assertEquals(1, catalog.totalSize());
    }

    @Test
    void snapshotsAreImmutableAndSortedById() {
        ContentCatalog catalog = new ContentCatalog();
        catalog.register(ContentTypes.ANIME,
                new AnimeDefinition(Identifier.fromNamespaceAndPath("plus-a", "b"), "B", ""), "plus-a");
        catalog.register(ContentTypes.ANIME,
                new AnimeDefinition(Identifier.fromNamespaceAndPath("plus-a", "a"), "A", ""), "plus-a");

        var snapshot = catalog.snapshot(ContentTypes.ANIME);
        assertEquals("A", snapshot.get(0).displayName());
        assertEquals("B", snapshot.get(1).displayName());
    }
}
