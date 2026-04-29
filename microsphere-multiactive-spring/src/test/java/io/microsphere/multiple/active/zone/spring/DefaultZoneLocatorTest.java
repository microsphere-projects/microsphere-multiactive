package io.microsphere.multiple.active.zone.spring;

import org.junit.jupiter.api.Test;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;

import java.util.HashMap;
import java.util.Map;

import static io.microsphere.multiple.active.zone.ZoneConstants.DEFAULT_ZONE;
import static io.microsphere.multiple.active.zone.ZoneConstants.ZONE_PROPERTY_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link DefaultZoneLocator} and base class {@link AbstractZoneLocator}
 */
class DefaultZoneLocatorTest {

    @Test
    void testDefaultOrder() {
        DefaultZoneLocator locator = new DefaultZoneLocator();
        assertEquals(DefaultZoneLocator.DEFAULT_ORDER, locator.getOrder());
        assertEquals(20, locator.getOrder());
    }

    @Test
    void testSetOrder() {
        DefaultZoneLocator locator = new DefaultZoneLocator();
        locator.setOrder(5);
        assertEquals(5, locator.getOrder());
    }

    @Test
    void testSupportsAlwaysTrue() {
        DefaultZoneLocator locator = new DefaultZoneLocator();
        assertTrue(locator.supports(new StandardEnvironment()));
    }

    @Test
    void testLocateWithZoneProperty() {
        DefaultZoneLocator locator = new DefaultZoneLocator();
        StandardEnvironment environment = new StandardEnvironment();

        Map<String, Object> props = new HashMap<>();
        props.put(ZONE_PROPERTY_NAME, "zone-a");
        MutablePropertySources sources = environment.getPropertySources();
        sources.addFirst(new MapPropertySource("test", props));

        String zone = locator.locate(environment);
        assertEquals("zone-a", zone);
    }

    @Test
    void testLocateWithoutZonePropertyFallsBackToDefault() {
        DefaultZoneLocator locator = new DefaultZoneLocator();
        StandardEnvironment environment = new StandardEnvironment();

        String zone = locator.locate(environment);
        assertEquals(DEFAULT_ZONE, zone);
    }

    @Test
    void testSetBeanName() {
        DefaultZoneLocator locator = new DefaultZoneLocator();
        locator.setBeanName("defaultZoneLocator");
        assertEquals("defaultZoneLocator", locator.beanName);
    }

    @Test
    void testToString() {
        DefaultZoneLocator locator = new DefaultZoneLocator();
        locator.setBeanName("defaultZoneLocator");
        String str = locator.toString();
        assertNotNull(str);
        assertTrue(str.contains("beanName='defaultZoneLocator'"));
        assertTrue(str.contains("order=20"));
    }

    @Test
    void testLoggerNotNull() {
        DefaultZoneLocator locator = new DefaultZoneLocator();
        assertNotNull(locator.logger);
    }
}
