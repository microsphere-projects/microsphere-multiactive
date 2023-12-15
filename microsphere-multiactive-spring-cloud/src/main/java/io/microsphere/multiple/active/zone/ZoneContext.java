package io.microsphere.multiple.active.zone;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Objects;

import static io.microsphere.multiple.active.zone.ZoneConstants.CURRENT_ZONE_PROPERTY_NAME;
import static io.microsphere.multiple.active.zone.ZoneConstants.DEFAULT_PREFERENCE_UPSTREAM_DISABLED_ZONE;
import static io.microsphere.multiple.active.zone.ZoneConstants.DEFAULT_PREFERENCE_UPSTREAM_SAME_ZONE_MIN_AVAILABLE;
import static io.microsphere.multiple.active.zone.ZoneConstants.DEFAULT_PREFERENCE_UPSTREAM_ZONE_READY_PERCENTAGE;
import static io.microsphere.multiple.active.zone.ZoneConstants.DEFAULT_ZONE;
import static io.microsphere.multiple.active.zone.ZoneConstants.DEFAULT_ZONE_ENABLED;
import static io.microsphere.multiple.active.zone.ZoneConstants.DEFAULT_ZONE_PREFERENCE_ENABLED;
import static io.microsphere.multiple.active.zone.ZoneConstants.DEFAULT_ZONE_PREFERENCE_FILTER_ORDER;
import static org.springframework.util.StringUtils.arrayToCommaDelimitedString;
import static org.springframework.util.StringUtils.commaDelimitedListToStringArray;
import static org.springframework.util.StringUtils.hasText;

/**
 * The context of zone
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class ZoneContext {

    private static final Logger logger = LoggerFactory.getLogger(ZoneContext.class);

    private static final ZoneContext instance = new ZoneContext();

    private volatile boolean enabled = DEFAULT_ZONE_ENABLED;

    private volatile String zone = DEFAULT_ZONE;

    private volatile boolean preferenceEnabled = DEFAULT_ZONE_PREFERENCE_ENABLED;

    private volatile int preferenceFilterOrder = DEFAULT_ZONE_PREFERENCE_FILTER_ORDER;

    private volatile int preferenceUpstreamZoneReadyPercentage = DEFAULT_PREFERENCE_UPSTREAM_ZONE_READY_PERCENTAGE;

    private volatile int preferenceUpstreamSameZoneMinAvailable = DEFAULT_PREFERENCE_UPSTREAM_SAME_ZONE_MIN_AVAILABLE;

    private volatile String preferenceUpstreamDisabledZone = DEFAULT_PREFERENCE_UPSTREAM_DISABLED_ZONE;

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public void setEnabled(boolean enabled) {
        if (isPropertyChanged("enabled", this.enabled, enabled)) {
            this.enabled = enabled;
        }
    }

    public void setZone(String zone) {
        if (isPropertyChanged("zone", this.zone, zone)) {
            this.zone = StringUtils.trim(zone);
        }
    }

    public void setPreferenceEnabled(boolean preferenceEnabled) {
        if (isPropertyChanged("preferenceEnabled", this.preferenceEnabled, preferenceEnabled)) {
            this.preferenceEnabled = preferenceEnabled;
        }
    }

    public void setPreferenceFilterOrder(int preferenceFilterOrder) {
        if (isPropertyChanged("preferenceFilterOrder", this.preferenceFilterOrder, preferenceFilterOrder)) {
            this.preferenceFilterOrder = preferenceFilterOrder;
        }
    }

    public void setPreferenceUpstreamZoneReadyPercentage(int preferenceUpstreamZoneReadyPercentage) {
        if (isPropertyChanged("preferenceUpstreamZoneReadyPercentage", this.preferenceUpstreamZoneReadyPercentage,
                preferenceUpstreamZoneReadyPercentage)) {
            this.preferenceUpstreamZoneReadyPercentage = preferenceUpstreamZoneReadyPercentage;
        }
    }

    public void setPreferenceUpstreamSameZoneMinAvailable(int preferenceUpstreamSameZoneMinAvailable) {
        if (isPropertyChanged("preferenceUpstreamSameZoneMinAvailable", this.preferenceUpstreamSameZoneMinAvailable,
                preferenceUpstreamSameZoneMinAvailable)) {
            this.preferenceUpstreamSameZoneMinAvailable = preferenceUpstreamSameZoneMinAvailable;
        }
    }

    public void setPreferenceUpstreamDisabledZone(String preferenceUpstreamDisabledZone) {
        if (isPropertyChanged("preferenceUpstreamDisabledZone", this.preferenceUpstreamDisabledZone, preferenceUpstreamDisabledZone)) {
            this.preferenceUpstreamDisabledZone = resolveCommaDelimited(preferenceUpstreamDisabledZone);
        }
    }

    private String resolveCommaDelimited(String value) {
        if (!hasText(value)) {
            return value;
        }

        String[] values = commaDelimitedListToStringArray(value);

        for (int i = 0; i < values.length; i++) {
            values[i] = StringUtils.trim(values[i]);
        }

        return arrayToCommaDelimitedString(values);
    }

    void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     * @return Zone context enabled or not
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @return current zone in the context
     */
    public String getZone() {
        return zone;
    }

    /**
     * @return Zone preference enabled or not
     */
    public boolean isPreferenceEnabled() {
        return preferenceEnabled;
    }

    /**
     * @return the order of zone preference filter
     */
    public int getPreferenceFilterOrder() {
        return preferenceFilterOrder;
    }

    /**
     * @return the zone ready percentage of upstream servers(nodes) for zone-preference
     */
    public int getPreferenceUpstreamZoneReadyPercentage() {
        return preferenceUpstreamZoneReadyPercentage;
    }

    /**
     * @return the mix available of upstream servers(nodes) in the same zone for zone-preference
     */
    public int getPreferenceUpstreamSameZoneMinAvailable() {
        return preferenceUpstreamSameZoneMinAvailable;
    }

    /**
     * @return the disabled zone of upstream servers(nodes) for zone-preference
     */
    public String getPreferenceUpstreamDisabledZone() {
        return preferenceUpstreamDisabledZone;
    }

    /**
     * Enable and returns previous enabled status
     *
     * @return previous enabled status
     */
    public boolean enable() {
        boolean enabled = this.enabled;
        if (!enabled) {
            setEnabled(true);
        }
        return enabled;
    }

    /**
     * Reset to default status
     */
    public void reset() {
        setEnabled(DEFAULT_ZONE_ENABLED);
        setZone(DEFAULT_ZONE);
        setPreferenceEnabled(DEFAULT_ZONE_PREFERENCE_ENABLED);
        setPreferenceFilterOrder(DEFAULT_ZONE_PREFERENCE_FILTER_ORDER);
        setPreferenceUpstreamZoneReadyPercentage(DEFAULT_PREFERENCE_UPSTREAM_ZONE_READY_PERCENTAGE);
        setPreferenceUpstreamSameZoneMinAvailable(DEFAULT_PREFERENCE_UPSTREAM_SAME_ZONE_MIN_AVAILABLE);
        setPreferenceUpstreamDisabledZone(DEFAULT_PREFERENCE_UPSTREAM_DISABLED_ZONE);
    }

    private boolean isPropertyChanged(String propertyName, Object previousPropertyValue, Object newPropertyValue) {
        boolean changed = !Objects.equals(previousPropertyValue, newPropertyValue);
        if (changed) {
            propertyChangeSupport.firePropertyChange(propertyName, previousPropertyValue, newPropertyValue);
            logger.info("The property value [name : '{}'] is changed from '{}' to '{}'", propertyName, previousPropertyValue, newPropertyValue);
        } else {
            logger.debug("The property value [name : '{}'] is not changed : '{}'", propertyName, previousPropertyValue);
        }
        return changed;
    }

    @Override
    public String toString() {
        return "ZoneContext{"
                + "enabled=" + enabled
                + ", zone='" + zone + '\''
                + ", preferenceEnabled=" + preferenceEnabled
                + ", preferenceFilterOrder=" + preferenceFilterOrder
                + ", preferenceUpstreamZoneReadyPercentage=" + preferenceUpstreamZoneReadyPercentage
                + ", preferenceUpstreamSameZoneMinAvailable=" + preferenceUpstreamSameZoneMinAvailable
                + ", preferenceUpstreamDisabledZone=" + preferenceUpstreamDisabledZone + '}';
    }

    /**
     * Get current {@link ZoneContext}
     * 
     * @return non-null
     */
    public static ZoneContext get() {
        return instance;
    }

    /**
     * Get current zone
     * 
     * @return non-null
     */
    public static String getCurrentZone() {
        return System.getProperty(CURRENT_ZONE_PROPERTY_NAME, DEFAULT_ZONE);
    }
}
