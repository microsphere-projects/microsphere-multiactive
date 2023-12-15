package io.microsphere.multiple.active.zone;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ApplicationContextEvent;

import java.beans.PropertyChangeEvent;
import java.util.List;

import static java.util.Collections.unmodifiableList;

/**
 * {@link ZoneContext} Changed {@link ApplicationEvent Event}
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class ZoneContextChangedEvent extends ApplicationContextEvent {

    private final ZoneContext zoneContext;

    private final List<PropertyChangeEvent> propertyChangeEvents;

    /**
     * Create a new ContextStartedEvent.
     * 
     * @param context the {@code ApplicationContext} that the event is raised for (must not be
     *        {@code null})
     * @param zoneContext {@link ZoneContext}
     * @param propertyChangeEvents {@link PropertyChangeEvent} List
     */
    public ZoneContextChangedEvent(ApplicationContext context, ZoneContext zoneContext, List<PropertyChangeEvent> propertyChangeEvents) {
        super(context);
        this.zoneContext = zoneContext;
        this.propertyChangeEvents = unmodifiableList(propertyChangeEvents);
    }

    public ZoneContext getZoneContext() {
        return zoneContext;
    }

    public List<PropertyChangeEvent> getPropertyChangeEvents() {
        return propertyChangeEvents;
    }
}
