package io.microsphere.multiple.active.zone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static io.microsphere.multiple.active.zone.ZoneConstants.DEFAULT_PREFERENCE_UPSTREAM_DISABLED_ZONE;
import static io.microsphere.multiple.active.zone.ZoneConstants.DEFAULT_PREFERENCE_UPSTREAM_SAME_ZONE_MIN_AVAILABLE;
import static io.microsphere.multiple.active.zone.ZoneConstants.DEFAULT_PREFERENCE_UPSTREAM_ZONE_READY_PERCENTAGE;
import static io.microsphere.multiple.active.zone.ZoneConstants.DEFAULT_ZONE_ENABLED;
import static io.microsphere.multiple.active.zone.ZoneConstants.DEFAULT_ZONE_PREFERENCE_ENABLED;
import static io.microsphere.multiple.active.zone.ZoneConstants.DEFAULT_ZONE_PREFERENCE_FILTER_ORDER;
import static io.microsphere.multiple.active.zone.ZoneConstants.ORIGINAL_ZONE;
import static io.microsphere.multiple.active.zone.ZoneConstants.PREFERENCE_ENABLED_PROPERTY_NAME;
import static io.microsphere.multiple.active.zone.ZoneConstants.PREFERENCE_FILTER_ORDER_PROPERTY_NAME;
import static io.microsphere.multiple.active.zone.ZoneConstants.PREFERENCE_UPSTREAM_DISABLED_ZONE_PROPERTY_NAME;
import static io.microsphere.multiple.active.zone.ZoneConstants.PREFERENCE_UPSTREAM_SAME_ZONE_MIN_AVAILABLE_PROPERTY_NAME;
import static io.microsphere.multiple.active.zone.ZoneConstants.PREFERENCE_UPSTREAM_ZONE_READY_PERCENTAGE_PROPERTY_NAME;
import static io.microsphere.multiple.active.zone.ZoneConstants.ZONE_ENABLED_PROPERTY_NAME;
import static io.microsphere.multiple.active.zone.ZoneConstants.ZONE_PROPERTY_NAME;

/**
 * A listener for {@link ApplicationStartedEvent} and {@link EnvironmentChangeEvent} to change the {@link ZoneContext}
 * 
 * @see ZoneContext
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class ZoneContextChangedListener implements SmartApplicationListener, ApplicationContextAware, EnvironmentAware {

    private static final Logger logger = LoggerFactory.getLogger(ZoneContextChangedListener.class);

    protected static final Class[] SUPPORTED_EVENT_TYPES = new Class[] {ApplicationStartedEvent.class, EnvironmentChangeEvent.class};

    protected static final List<String> ZONE_CONTEXT_PROPERTY_NAMES = Arrays.asList(
            ZONE_PROPERTY_NAME,
            ZONE_ENABLED_PROPERTY_NAME,
            PREFERENCE_ENABLED_PROPERTY_NAME,
            PREFERENCE_FILTER_ORDER_PROPERTY_NAME,
            PREFERENCE_UPSTREAM_ZONE_READY_PERCENTAGE_PROPERTY_NAME,
            PREFERENCE_UPSTREAM_SAME_ZONE_MIN_AVAILABLE_PROPERTY_NAME,
            PREFERENCE_UPSTREAM_DISABLED_ZONE_PROPERTY_NAME
    );

    protected final Map<String, Consumer<String>> propertyChangedHandlers = new HashMap<>(ZONE_CONTEXT_PROPERTY_NAMES.size());

    public ZoneContextChangedListener() {
        initPropertyChangedHandlers();
    }

    private ApplicationContext context;

    private Environment environment;

    private ZoneContext zoneContext;

    private ZoneLocator zoneLocator;

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return ObjectUtils.containsElement(SUPPORTED_EVENT_TYPES, eventType);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationStartedEvent) {
            onApplicationStartedEvent();
        } else if (event instanceof EnvironmentChangeEvent) {
            onEnvironmentChangeEvent((EnvironmentChangeEvent) event);
        }
    }

    private void initPropertyChangedHandlers() {
        propertyChangedHandlers.put(ZONE_PROPERTY_NAME, this::changeZone);
        propertyChangedHandlers.put(ZONE_ENABLED_PROPERTY_NAME, this::changeEnabled);
        propertyChangedHandlers.put(PREFERENCE_ENABLED_PROPERTY_NAME, this::changeZonePreferenceEnabled);
        propertyChangedHandlers.put(PREFERENCE_FILTER_ORDER_PROPERTY_NAME, this::changePreferenceFilterOrder);
        propertyChangedHandlers.put(PREFERENCE_UPSTREAM_ZONE_READY_PERCENTAGE_PROPERTY_NAME, this::changePreferenceUpstreamZoneReadyPercentage);
        propertyChangedHandlers.put(PREFERENCE_UPSTREAM_SAME_ZONE_MIN_AVAILABLE_PROPERTY_NAME, this::changePreferenceUpstreamSameZoneMinAvailable);
        propertyChangedHandlers.put(PREFERENCE_UPSTREAM_DISABLED_ZONE_PROPERTY_NAME, this::changePreferenceUpstreamDisabledZone);
    }

    private void onApplicationStartedEvent() {
        changeZoneContext(ZONE_CONTEXT_PROPERTY_NAMES);
    }

    private void onEnvironmentChangeEvent(EnvironmentChangeEvent event) {
        Set<String> propertyNames = event.getKeys();
        changeZoneContext(propertyNames);
    }

    private void changeZoneContext(Iterable<String> propertyNames) {

        List<PropertyChangeEvent> propertyChangeEvents = new LinkedList<>();

        PropertyChangeListener listener = propertyChangeEvents::add;

        try {
            // Add PropertyChangeListener temporarily
            zoneContext.addPropertyChangeListener(listener);
            // Handle property changed if matched
            for (String propertyName : propertyNames) {
                Consumer<String> propertyChangedHandler = propertyChangedHandlers.get(propertyName);
                if (propertyChangedHandler != null) {
                    propertyChangedHandler.accept(propertyName);
                }
            }
        } finally {
            // Remove PropertyChangeListener finally
            zoneContext.removePropertyChangeListener(listener);
        }

        boolean changed = !propertyChangeEvents.isEmpty();

        if (changed) {
            publishZoneContextChangedEvent(propertyChangeEvents);
        }
    }

    private void publishZoneContextChangedEvent(List<PropertyChangeEvent> propertyChangeEvents) {
        logger.info("Current ApplicationContext[id : {}] is publishing a ZoneContextChangedEvent : {}", context.getId(), propertyChangeEvents);
        context.publishEvent(new ZoneContextChangedEvent(context, zoneContext, propertyChangeEvents));
    }

    private void changeZone(String propertyName) {
        String zone = getProperty(propertyName, ORIGINAL_ZONE);
        if (StringUtils.hasText(zone)) {
            // Revert to original zone
            if (ORIGINAL_ZONE.equalsIgnoreCase(zone)) {
                zone = revertOriginalZone();
            }
            zoneContext.setZone(zone);
        }
    }

    private String revertOriginalZone() {
        String originalZone = ZoneConstants.DEFAULT_ZONE;
        if (zoneLocator.supports(environment)) {
            originalZone = zoneLocator.locate(environment);
        } else {
            logger.warn("The zone can't be discovered by {}", zoneLocator);
        }
        logger.info("Revert zone ['{}'] into the {} by {}", originalZone, zoneContext, zoneLocator);
        return originalZone;
    }

    private void changeEnabled(String propertyName) {
        boolean enabled = getProperty(propertyName, boolean.class, DEFAULT_ZONE_ENABLED);
        zoneContext.setEnabled(enabled);
    }

    private void changeZonePreferenceEnabled(String propertyName) {
        boolean preferenceEnabled = getProperty(propertyName, boolean.class, DEFAULT_ZONE_PREFERENCE_ENABLED);
        zoneContext.setPreferenceEnabled(preferenceEnabled);
    }

    private void changePreferenceFilterOrder(String propertyName) {
        int preferenceFilterOrder = getProperty(propertyName, int.class, DEFAULT_ZONE_PREFERENCE_FILTER_ORDER);
        zoneContext.setPreferenceFilterOrder(preferenceFilterOrder);
    }

    private void changePreferenceUpstreamZoneReadyPercentage(String propertyName) {
        int preferenceUpstreamZoneReadyPercentage = getProperty(propertyName, int.class, DEFAULT_PREFERENCE_UPSTREAM_ZONE_READY_PERCENTAGE);
        zoneContext.setPreferenceUpstreamZoneReadyPercentage(preferenceUpstreamZoneReadyPercentage);
    }

    private void changePreferenceUpstreamSameZoneMinAvailable(String propertyName) {
        int preferenceUpstreamSameZoneMinAvailable = getProperty(propertyName, int.class, DEFAULT_PREFERENCE_UPSTREAM_SAME_ZONE_MIN_AVAILABLE);
        zoneContext.setPreferenceUpstreamSameZoneMinAvailable(preferenceUpstreamSameZoneMinAvailable);
    }

    private void changePreferenceUpstreamDisabledZone(String propertyName) {
        String preferenceUpstreamDisabledZone = getProperty(propertyName, DEFAULT_PREFERENCE_UPSTREAM_DISABLED_ZONE);
        zoneContext.setPreferenceUpstreamDisabledZone(preferenceUpstreamDisabledZone);
    }

    private String getProperty(String propertyName, String defaultValue) {
        return getProperty(propertyName, String.class, defaultValue);
    }

    private <V> V getProperty(String propertyName, Class<V> propertyType, V defaultValue) {
        V value = environment.getProperty(propertyName, propertyType);
        if (value == null) {
            logger.info("The property [name : '{}'] is not found or deleted , the default value ['{}'] will be set", propertyName, defaultValue);
            value = defaultValue;
        } else {
            logger.info("The property [name : '{}' , value : '{}'] will be set", propertyName, value);
        }
        return value;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
        this.zoneContext = context.getBean(ZoneContext.class);
        this.zoneLocator = context.getBean(ZoneLocator.class);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

}
