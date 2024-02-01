package io.microsphere.multiple.active.zone.spring.event;

import io.microsphere.multiple.active.zone.ZoneConstants;
import io.microsphere.multiple.active.zone.ZoneContext;
import io.microsphere.multiple.active.zone.spring.ZoneLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import static io.microsphere.util.ClassLoaderUtils.getClassLoader;
import static io.microsphere.util.ClassLoaderUtils.resolveClass;

/**
 * A listener to change the {@link ZoneContext} on one of those being raised :
 * <ul>
 *     <li>{@link #APPLICATION_STARTED_EVENT_CLASS_NAME org.springframework.boot.context.event.ApplicationStartedEvent}</li>
 *     <li>{@link #ENVIRONMENT_CHANGE_EVENT_CLASS_NAME org.springframework.cloud.context.environment.EnvironmentChangeEvent}</li>
 *     <li>{@link ContextRefreshedEvent}</li>
 * </ul>
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see ZoneContext
 * @since 1.0.0
 */
public class ZoneContextChangedListener implements SmartApplicationListener, ApplicationContextAware, EnvironmentAware {

    private static final Class<ZoneContextChangedListener> CURRENT_CLASS = ZoneContextChangedListener.class;

    private static final Logger logger = LoggerFactory.getLogger(CURRENT_CLASS);

    private static final ClassLoader classLoader = getClassLoader(CURRENT_CLASS);

    /**
     * ApplicationEvent for Spring Boot
     */
    private static final String APPLICATION_STARTED_EVENT_CLASS_NAME = "org.springframework.boot.context.event.ApplicationStartedEvent";

    /**
     * ApplicationEvent for Spring Cloud
     */
    private static final String ENVIRONMENT_CHANGE_EVENT_CLASS_NAME = "org.springframework.cloud.context.environment.EnvironmentChangeEvent";

    private static final Class<?> APPLICATION_STARTED_EVENT_CLASS = resolveClass(APPLICATION_STARTED_EVENT_CLASS_NAME, classLoader);

    private static final Class<?> ENVIRONMENT_CHANGE_EVENT_CLASS = resolveClass(ENVIRONMENT_CHANGE_EVENT_CLASS_NAME, classLoader);

    private static final Class<?> CONTEXT_REFRESHED_EVENT_CLASS = ContextRefreshedEvent.class;

    private static final boolean IS_SPRING_CLOUD_APPLICATION = ENVIRONMENT_CHANGE_EVENT_CLASS != null && APPLICATION_STARTED_EVENT_CLASS != null;

    private static final boolean IS_SPRING_BOOT_APPLICATION = ENVIRONMENT_CHANGE_EVENT_CLASS == null && APPLICATION_STARTED_EVENT_CLASS != null;

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
        if (IS_SPRING_CLOUD_APPLICATION) {
            return ENVIRONMENT_CHANGE_EVENT_CLASS.isAssignableFrom(eventType) ||
                    APPLICATION_STARTED_EVENT_CLASS.isAssignableFrom(eventType);
        } else if (IS_SPRING_BOOT_APPLICATION) {
            return APPLICATION_STARTED_EVENT_CLASS.isAssignableFrom(eventType);
        } else {
            return CONTEXT_REFRESHED_EVENT_CLASS.isAssignableFrom(eventType);
        }
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        tryChangeZoneContext();
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

    private void tryChangeZoneContext() {
        changeZoneContext(ZONE_CONTEXT_PROPERTY_NAMES);
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
