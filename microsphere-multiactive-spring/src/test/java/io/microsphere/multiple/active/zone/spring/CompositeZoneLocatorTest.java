package io.microsphere.multiple.active.zone.spring;

import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.microsphere.multiple.active.zone.ZoneConstants.LOCATOR_FAST_FAIL_PROPERTY_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link CompositeZoneLocator}
 */
class CompositeZoneLocatorTest {

    /** Always supports and returns a fixed zone */
    static ZoneLocator fixedLocator(String zone) {
        return new ZoneLocator() {
            @Override
            public boolean supports(Environment env) {
                return true;
            }

            @Override
            public String locate(Environment env) {
                return zone;
            }

            @Override
            public String toString() {
                return "FixedLocator[" + zone + "]";
            }
        };
    }

    /** Never supports */
    static ZoneLocator unsupportedLocator() {
        return new ZoneLocator() {
            @Override
            public boolean supports(Environment env) {
                return false;
            }

            @Override
            public String locate(Environment env) {
                return "should-not-be-called";
            }
        };
    }

    /** Supports but returns null */
    static ZoneLocator nullLocator() {
        return new ZoneLocator() {
            @Override
            public boolean supports(Environment env) {
                return true;
            }

            @Override
            public String locate(Environment env) {
                return null;
            }
        };
    }

    /** Supports but throws */
    static ZoneLocator throwingLocator() {
        return new ZoneLocator() {
            @Override
            public boolean supports(Environment env) {
                return true;
            }

            @Override
            public String locate(Environment env) {
                throw new RuntimeException("locate error");
            }
        };
    }

    static Environment newEnvironment() {
        return new StandardEnvironment();
    }

    static Environment newFastFailEnvironment() {
        StandardEnvironment env = new StandardEnvironment();
        Map<String, Object> props = new HashMap<>();
        props.put(LOCATOR_FAST_FAIL_PROPERTY_NAME, "true");
        env.getPropertySources().addFirst(new MapPropertySource("test", props));
        return env;
    }

    @Test
    void testConstructorWithNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> new CompositeZoneLocator(null));
    }

    @Test
    void testSupportsWhenAtLeastOneSupports() {
        CompositeZoneLocator composite = new CompositeZoneLocator(
                Arrays.asList(unsupportedLocator(), fixedLocator("zone-a"))
        );
        assertTrue(composite.supports(newEnvironment()));
    }

    @Test
    void testSupportsWhenNoneSupport() {
        CompositeZoneLocator composite = new CompositeZoneLocator(
                Arrays.asList(unsupportedLocator(), unsupportedLocator())
        );
        assertFalse(composite.supports(newEnvironment()));
    }

    @Test
    void testLocateWithFirstMatchingLocator() {
        CompositeZoneLocator composite = new CompositeZoneLocator(
                Arrays.asList(fixedLocator("zone-a"), fixedLocator("zone-b"))
        );
        String zone = composite.locate(newEnvironment());
        assertEquals("zone-a", zone);
    }

    @Test
    void testLocateCachesResult() {
        CompositeZoneLocator composite = new CompositeZoneLocator(
                Collections.singletonList(fixedLocator("zone-cached"))
        );
        Environment env = newEnvironment();
        String zone1 = composite.locate(env);
        String zone2 = composite.locate(env); // should return cached
        assertEquals("zone-cached", zone1);
        assertEquals("zone-cached", zone2);
    }

    @Test
    void testLocateReturnsNullWhenNoneLocates() {
        CompositeZoneLocator composite = new CompositeZoneLocator(
                Arrays.asList(nullLocator(), nullLocator())
        );
        String zone = composite.locate(newEnvironment());
        assertNull(zone);
    }
    @Test
    void testLocateSkipsUnsupportedLocators() {
        CompositeZoneLocator composite = new CompositeZoneLocator(
                Arrays.asList(unsupportedLocator(), fixedLocator("zone-b"))
        );
        String zone = composite.locate(newEnvironment());
        assertEquals("zone-b", zone);
    }

    @Test
    void testLocateWithFastFailOnNullZone() {
        CompositeZoneLocator composite = new CompositeZoneLocator(
                Collections.singletonList(nullLocator())
        );
        assertThrows(IllegalStateException.class, () -> composite.locate(newFastFailEnvironment()));
    }

    @Test
    void testLocateWithFastFailOnException() {
        CompositeZoneLocator composite = new CompositeZoneLocator(
                Collections.singletonList(throwingLocator())
        );
        assertThrows(IllegalStateException.class, () -> composite.locate(newFastFailEnvironment()));
    }

    @Test
    void testLocateWithExceptionNoFastFail() {
        // Exception is logged but execution continues
        CompositeZoneLocator composite = new CompositeZoneLocator(
                Arrays.asList(throwingLocator(), fixedLocator("zone-fallback"))
        );
        String zone = composite.locate(newEnvironment());
        assertEquals("zone-fallback", zone);
    }

    @Test
    void testToString() {
        CompositeZoneLocator composite = new CompositeZoneLocator(
                Collections.singletonList(fixedLocator("zone-x"))
        );
        String str = composite.toString();
        assertNotNull(str);
        assertTrue(str.contains("CompositeZoneLocator{"));
    }

    @Test
    void testLocateWithEmptyLocatorList() {
        CompositeZoneLocator composite = new CompositeZoneLocator(Collections.emptyList());
        assertFalse(composite.supports(newEnvironment()));
        String zone = composite.locate(newEnvironment());
        assertNull(zone);
    }
}
