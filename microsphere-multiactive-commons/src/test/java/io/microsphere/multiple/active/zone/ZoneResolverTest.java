package io.microsphere.multiple.active.zone;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests for {@link ZoneResolver} default method.
 */
class ZoneResolverTest {

    @Test
    void testApplyDelegatesToResolve() {
        ZoneResolver<String> resolver = entity -> entity + "-zone";
        // apply() is the default method that delegates to resolve()
        assertEquals("test-zone", resolver.apply("test"));
        assertEquals("test-zone", resolver.resolve("test"));
    }

    @Test
    void testApplyWithNullEntity() {
        ZoneResolver<String> resolver = entity -> entity;
        assertNull(resolver.apply(null));
    }
}
