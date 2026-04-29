package io.microsphere.multiple.active.zone.spring.event;

import io.microsphere.multiple.active.zone.ZoneContext;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.StaticApplicationContext;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests for {@link ZoneContextChangedEvent}
 */
class ZoneContextChangedEventTest {

    @Test
    void testGetZoneContext() {
        ApplicationContext context = new StaticApplicationContext();
        ZoneContext zoneContext = ZoneContext.get();
        List<PropertyChangeEvent> events = Arrays.asList(
                new PropertyChangeEvent(zoneContext, "zone", "old-zone", "new-zone")
        );

        ZoneContextChangedEvent event = new ZoneContextChangedEvent(context, zoneContext, events);

        assertSame(zoneContext, event.getZoneContext());
    }

    @Test
    void testGetPropertyChangeEvents() {
        ApplicationContext context = new StaticApplicationContext();
        ZoneContext zoneContext = ZoneContext.get();
        PropertyChangeEvent pce1 = new PropertyChangeEvent(zoneContext, "zone", "old-zone", "new-zone");
        PropertyChangeEvent pce2 = new PropertyChangeEvent(zoneContext, "enabled", true, false);
        List<PropertyChangeEvent> events = Arrays.asList(pce1, pce2);

        ZoneContextChangedEvent event = new ZoneContextChangedEvent(context, zoneContext, events);

        List<PropertyChangeEvent> result = event.getPropertyChangeEvents();
        assertEquals(2, result.size());
        assertSame(pce1, result.get(0));
        assertSame(pce2, result.get(1));
    }

    @Test
    void testGetSource() {
        StaticApplicationContext context = new StaticApplicationContext();
        ZoneContext zoneContext = ZoneContext.get();
        List<PropertyChangeEvent> events = Arrays.asList(
                new PropertyChangeEvent(zoneContext, "zone", "a", "b")
        );

        ZoneContextChangedEvent event = new ZoneContextChangedEvent(context, zoneContext, events);
        assertSame(context, event.getSource());
        assertSame(context, event.getApplicationContext());
    }
}
