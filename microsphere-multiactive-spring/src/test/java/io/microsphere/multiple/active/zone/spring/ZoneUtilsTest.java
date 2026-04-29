package io.microsphere.multiple.active.zone.spring;

import io.microsphere.multiple.active.zone.ZoneContext;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests for {@link ZoneUtils}
 */
class ZoneUtilsTest {

    @Test
    void testGetZoneContext() {
        StaticApplicationContext context = new StaticApplicationContext();
        ZoneContext zoneContext = ZoneContext.get();
        context.getBeanFactory().registerSingleton(ZoneUtils.ZONE_CONTEXT_BEAN_NAME, zoneContext);

        ZoneContext result = ZoneUtils.getZoneContext(context.getBeanFactory());
        assertNotNull(result);
        assertSame(zoneContext, result);
    }

    @Test
    void testGetZoneLocator() {
        StaticApplicationContext context = new StaticApplicationContext();

        ZoneLocator locator = new DefaultZoneLocator();
        context.getBeanFactory().registerSingleton(ZoneUtils.ZONE_LOCATOR_BEAN_NAME, locator);

        ZoneLocator result = ZoneUtils.getZoneLocator(context.getBeanFactory());
        assertNotNull(result);
        assertSame(locator, result);
    }
}
