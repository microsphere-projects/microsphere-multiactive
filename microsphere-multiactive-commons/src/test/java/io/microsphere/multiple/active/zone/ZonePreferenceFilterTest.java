package io.microsphere.multiple.active.zone;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests for {@link ZonePreferenceFilter}
 */
class ZonePreferenceFilterTest {

    /** Simple entity wrapping a zone label */
    static class ZoneEntity {
        final String zone;

        ZoneEntity(String zone) {
            this.zone = zone;
        }

        @Override
        public String toString() {
            return "ZoneEntity{zone='" + zone + "'}";
        }
    }

    /** Resolver that returns the entity's zone field */
    private final ZoneResolver<ZoneEntity> resolver = entity -> entity == null ? null : entity.zone;

    private ZoneContext zoneContext;
    private ZonePreferenceFilter<ZoneEntity> filter;

    @BeforeEach
    void setUp() {
        zoneContext = ZoneContext.get();
        zoneContext.reset();
        // Enable zone + preference by default for most tests
        zoneContext.setEnabled(true);
        zoneContext.setPreferenceEnabled(true);
        zoneContext.setZone("zone-a");
        zoneContext.setPreferenceUpstreamZoneReadyPercentage(100);
        zoneContext.setPreferenceUpstreamSameZoneMinAvailable(1);
        filter = new ZonePreferenceFilter<>(zoneContext, resolver);
    }

    @AfterEach
    void tearDown() {
        zoneContext.reset();
    }

    // ---- edge cases ----

    @Test
    void testFilterNullList() {
        assertNull(filter.filter(null));
    }

    @Test
    void testFilterEmptyList() {
        List<ZoneEntity> empty = Collections.emptyList();
        assertSame(empty, filter.filter(empty));
    }

    @Test
    void testFilterSingleElementList() {
        List<ZoneEntity> single = Collections.singletonList(new ZoneEntity("zone-a"));
        assertSame(single, filter.filter(single));
    }

    // ---- disabled zone context ----

    @Test
    void testFilterWhenZoneContextDisabled() {
        zoneContext.setEnabled(false);
        List<ZoneEntity> entities = Arrays.asList(new ZoneEntity("zone-a"), new ZoneEntity("zone-b"));
        assertSame(entities, filter.filter(entities));
    }

    // ---- preference disabled ----

    @Test
    void testFilterWhenPreferenceDisabled() {
        zoneContext.setPreferenceEnabled(false);
        List<ZoneEntity> entities = Arrays.asList(new ZoneEntity("zone-a"), new ZoneEntity("zone-b"));
        assertSame(entities, filter.filter(entities));
    }

    // ---- ignored zone (blank or defaultZone) ----

    @Test
    void testFilterIgnoredWhenZoneIsDefault() {
        zoneContext.setZone("defaultZone");
        List<ZoneEntity> entities = Arrays.asList(new ZoneEntity("zone-a"), new ZoneEntity("zone-b"));
        assertSame(entities, filter.filter(entities));
    }

    @Test
    void testFilterIgnoredWhenZoneIsBlank() {
        zoneContext.setZone("  ");
        List<ZoneEntity> entities = Arrays.asList(new ZoneEntity("zone-a"), new ZoneEntity("zone-b"));
        assertSame(entities, filter.filter(entities));
    }

    @Test
    void testFilterIgnoredWhenZoneIsDEFAULTZONECaseInsensitive() {
        zoneContext.setZone("DEFAULTZONE");
        List<ZoneEntity> entities = Arrays.asList(new ZoneEntity("zone-a"), new ZoneEntity("zone-b"));
        assertSame(entities, filter.filter(entities));
    }

    // ---- normal filtering ----

    @Test
    void testFilterReturnsMatchingZoneEntities() {
        List<ZoneEntity> entities = Arrays.asList(
                new ZoneEntity("zone-a"),
                new ZoneEntity("zone-b"),
                new ZoneEntity("zone-a")
        );
        List<ZoneEntity> result = filter.filter(entities);
        assertEquals(2, result.size());
        result.forEach(e -> assertEquals("zone-a", e.zone));
    }

    @Test
    void testFilterReturnsAllWhenNoSameZoneFound() {
        List<ZoneEntity> entities = Arrays.asList(
                new ZoneEntity("zone-b"),
                new ZoneEntity("zone-c")
        );
        List<ZoneEntity> result = filter.filter(entities);
        assertSame(entities, result);
    }

    // ---- upstream zone not ready ----

    @Test
    void testFilterUpstreamZoneNotReady() {
        // Only 1 entity has a zone out of 2, so 50% < 100% threshold
        zoneContext.setPreferenceUpstreamZoneReadyPercentage(100);
        List<ZoneEntity> entities = Arrays.asList(
                new ZoneEntity("zone-a"),
                new ZoneEntity(null) // null zone
        );
        List<ZoneEntity> result = filter.filter(entities);
        // entities without zone are included (targetEntities), returns all
        assertEquals(2, result.size());
    }

    // ---- same zone min available threshold ----

    @Test
    void testFilterUnderSameZoneMinAvailableThreshold() {
        zoneContext.setPreferenceUpstreamSameZoneMinAvailable(5);
        // Only 1 same-zone entity, below threshold of 5
        List<ZoneEntity> entities = Arrays.asList(
                new ZoneEntity("zone-a"),
                new ZoneEntity("zone-b"),
                new ZoneEntity("zone-b")
        );
        // only 1 matches zone-a (below threshold 5)
        List<ZoneEntity> result = filter.filter(entities);
        assertSame(entities, result);
    }

    // ---- disabled zone filtering ----

    @Test
    void testFilterWithDisabledZone() {
        zoneContext.setPreferenceUpstreamDisabledZone("zone-c");
        List<ZoneEntity> entities = Arrays.asList(
                new ZoneEntity("zone-a"),
                new ZoneEntity("zone-b"),
                new ZoneEntity("zone-c"),
                new ZoneEntity("zone-a")
        );
        List<ZoneEntity> result = filter.filter(entities);
        // zone-c is disabled, so only zone-a entities should be in result
        assertEquals(2, result.size());
        result.forEach(e -> assertEquals("zone-a", e.zone));
    }

    @Test
    void testFilterWithDisabledZoneMultiple() {
        zoneContext.setPreferenceUpstreamDisabledZone("zone-b,zone-c");
        List<ZoneEntity> entities = Arrays.asList(
                new ZoneEntity("zone-a"),
                new ZoneEntity("zone-b"),
                new ZoneEntity("zone-c"),
                new ZoneEntity("zone-a")
        );
        List<ZoneEntity> result = filter.filter(entities);
        assertEquals(2, result.size());
    }

    @Test
    void testFilterWithDisabledZoneResultingInNotEnoughEntities() {
        // After disabling zones, only 1 entity remains → return original list
        zoneContext.setPreferenceUpstreamDisabledZone("zone-b");
        List<ZoneEntity> entities = Arrays.asList(
                new ZoneEntity("zone-a"),
                new ZoneEntity("zone-b")
        );
        List<ZoneEntity> result = filter.filter(entities);
        // only 1 entity after filter → fallback to original list
        assertSame(entities, result);
    }

    @Test
    void testGetOrder() {
        assertEquals(zoneContext.getPreferenceFilterOrder(), filter.getOrder());
    }

    // ---- null entity in list ----

    @Test
    void testFilterWithNullEntityInList() {
        List<ZoneEntity> entities = Arrays.asList(
                new ZoneEntity("zone-a"),
                null,
                new ZoneEntity("zone-b")
        );
        // null entity has null zone, so zoneCount=2 out of 3 -> 66% < 100% threshold
        List<ZoneEntity> result = filter.filter(entities);
        // zone-a matches, but upstream not ready (66% < 100%)
        assertSame(entities, result);
    }

    @Test
    void testFilterWithNullEntityAndLowerThreshold() {
        zoneContext.setPreferenceUpstreamZoneReadyPercentage(50);
        List<ZoneEntity> entities = Arrays.asList(
                new ZoneEntity("zone-a"),
                null,
                new ZoneEntity("zone-b")
        );
        // 2 out of 3 have zone (66%) >= 50% threshold; zone-a matches
        List<ZoneEntity> result = filter.filter(entities);
        assertEquals(1, result.size());
        assertEquals("zone-a", result.get(0).zone);
    }

    // ---- isIgnored branch: blank zone (empty string) ----

    @Test
    void testFilterIgnoredWhenZoneIsEmpty() {
        zoneContext.setZone("some-zone");  // first set to non-default to allow setZone to take effect
        // Now reset and manually set blank-like zone by setZone with an empty trimmed value
        // We cannot set " " directly because setZone trims it, resulting in "" which isBlank
        zoneContext.setZone("");
        List<ZoneEntity> entities = Arrays.asList(new ZoneEntity("zone-a"), new ZoneEntity("zone-b"));
        assertSame(entities, filter.filter(entities));
    }
}
