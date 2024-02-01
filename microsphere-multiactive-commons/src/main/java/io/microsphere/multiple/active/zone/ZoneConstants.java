package io.microsphere.multiple.active.zone;


import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public interface ZoneConstants {

    String ENABLED_PROPERTY_NAME_SUFFIX = ".enabled";

    /**
     * The property name of zone.
     */
    String ZONE_PROPERTY_NAME = "microsphere.availability.zone";

    /**
     * The property name of current zone.
     */
    String CURRENT_ZONE_PROPERTY_NAME = "microsphere.current.availability.zone";

    /**
     * The property name of zone that is enabled or not.
     */
    String ZONE_ENABLED_PROPERTY_NAME = ZONE_PROPERTY_NAME + ENABLED_PROPERTY_NAME_SUFFIX;

    // Zone Preference Properties

    String PREFERENCE_PROPERTY_NAME_PREFIX = ZONE_PROPERTY_NAME + ".preference";

    String PREFERENCE_ENABLED_PROPERTY_NAME = PREFERENCE_PROPERTY_NAME_PREFIX + ENABLED_PROPERTY_NAME_SUFFIX;

    String PREFERENCE_FILER_PROPERTY_NAME_PREFIX = PREFERENCE_PROPERTY_NAME_PREFIX + ".filter";

    String PREFERENCE_FILTER_ORDER_PROPERTY_NAME = PREFERENCE_FILER_PROPERTY_NAME_PREFIX + ".order";

    String PREFERENCE_UPSTREAM_PROPERTY_NAME_PREFIX = PREFERENCE_PROPERTY_NAME_PREFIX + ".upstream";

    String PREFERENCE_UPSTREAM_ZONE_READY_PERCENTAGE_PROPERTY_NAME = PREFERENCE_UPSTREAM_PROPERTY_NAME_PREFIX + ".zone-ready-percentage";

    String PREFERENCE_UPSTREAM_SAME_ZONE_MIN_AVAILABLE_PROPERTY_NAME = PREFERENCE_UPSTREAM_PROPERTY_NAME_PREFIX + ".same-zone-min-available";

    String PREFERENCE_UPSTREAM_DISABLED_ZONE_PROPERTY_NAME = PREFERENCE_UPSTREAM_PROPERTY_NAME_PREFIX + ".disabled-zone";

    // Zone Locator Properties

    String LOCATOR_PROPERTY_NAME_PREFIX = ZONE_PROPERTY_NAME + ".locator";

    String LOCATOR_FAST_FAIL_PROPERTY_NAME = LOCATOR_PROPERTY_NAME_PREFIX + ".fast-fail";

    String LOCATOR_TIMEOUT_PROPERTY_NAME = LOCATOR_PROPERTY_NAME_PREFIX + ".timeout";


    // Default values

    /**
     * The default value of zone that is enabled or not.
     */
    boolean DEFAULT_ZONE_ENABLED = true;

    boolean DEFAULT_ZONE_PREFERENCE_ENABLED = Boolean.getBoolean(PREFERENCE_ENABLED_PROPERTY_NAME);

    int DEFAULT_ZONE_PREFERENCE_FILTER_ORDER = 10;

    /**
     * The default value of zone that is compatible with Eureka zone.
     *
     * @see org.springframework.cloud.netflix.eureka.EurekaClientConfigBean#DEFAULT_ZONE
     */
    String DEFAULT_ZONE = "defaultZone";

    /**
     * The value of zone is used to revert the original zone where {@link ZoneLocator} locates
     */
    String ORIGINAL_ZONE = "originalZone";

    int DEFAULT_PREFERENCE_UPSTREAM_ZONE_READY_PERCENTAGE = 100;

    int DEFAULT_PREFERENCE_UPSTREAM_SAME_ZONE_MIN_AVAILABLE = 5;

    String DEFAULT_PREFERENCE_UPSTREAM_DISABLED_ZONE = null;

    boolean DEFAULT_LOCATOR_FAST_FAIL = false;

    int DEFAULT_TIMEOUT = (int) TimeUnit.SECONDS.toMillis(3);

}
