package io.microsphere.multiple.active.zone;

import io.microsphere.annotation.ConfigurationProperty;

import static io.microsphere.annotation.ConfigurationProperty.APPLICATION_SOURCE;
import static io.microsphere.constants.PropertyConstants.ENABLED_PROPERTY_NAME;
import static io.microsphere.constants.SymbolConstants.DOT;
import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;

/**
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public interface ZoneConstants {

    // Zone Properties

    /**
     * The property name suffix of enabled : ".enabled"
     */
    String ENABLED_PROPERTY_NAME_SUFFIX = DOT + ENABLED_PROPERTY_NAME;

    /**
     * The default value of zone that is compatible with Eureka zone.
     *
     * @see org.springframework.cloud.netflix.eureka.EurekaClientConfigBean#DEFAULT_ZONE
     */
    String DEFAULT_ZONE = "defaultZone";

    /**
     * The value of zone is used to revert the original zone where
     * {@link io.microsphere.multiple.active.zone.spring.ZoneLocator} locates
     *
     * @see io.microsphere.multiple.active.zone.spring.ZoneLocator
     */
    String ORIGINAL_ZONE = "originalZone";

    /**
     * The property name of zone : "microsphere.availability.zone"
     */
    @ConfigurationProperty(
            defaultValue = DEFAULT_ZONE,
            source = APPLICATION_SOURCE
    )
    String ZONE_PROPERTY_NAME = "microsphere.availability.zone";

    /**
     * The property name of current zone : "microsphere.current.availability.zone"
     */
    @ConfigurationProperty(
            source = APPLICATION_SOURCE
    )
    String CURRENT_ZONE_PROPERTY_NAME = "microsphere.current.availability.zone";

    /**
     * The property value of default zone that is enabled or not.
     */
    String DEFAULT_ZONE_ENABLED_PROPERTY_VALUE = "true";

    /**
     * The default value of zone that is enabled or not.
     */
    boolean DEFAULT_ZONE_ENABLED = parseBoolean(DEFAULT_ZONE_ENABLED_PROPERTY_VALUE);

    /**
     * The property name of zone that is enabled or not : "microsphere.availability.zone.enabled"
     */
    @ConfigurationProperty(
            type = boolean.class,
            defaultValue = DEFAULT_ZONE_ENABLED_PROPERTY_VALUE,
            source = APPLICATION_SOURCE
    )
    String ZONE_ENABLED_PROPERTY_NAME = ZONE_PROPERTY_NAME + ENABLED_PROPERTY_NAME_SUFFIX;

    // Zone Preference Properties

    /**
     * The property name prefix of zone preference : "microsphere.availability.zone.preference."
     */
    String PREFERENCE_PROPERTY_NAME_PREFIX = ZONE_PROPERTY_NAME + DOT + "preference" + DOT;

    /**
     * The porperty value of default value of zone preference that is enabled or not.
     */
    String DEFAULT_PREFERENCE_ENABLED_PROPERTY_VALUE = "false";

    /**
     * The default value of zone preference that is enabled or not.
     */
    boolean DEFAULT_ZONE_PREFERENCE_ENABLED = parseBoolean(DEFAULT_PREFERENCE_ENABLED_PROPERTY_VALUE);

    /**
     * The property name of zone preference enabled or not : "microsphere.availability.zone.preference.enabled"
     */
    @ConfigurationProperty(
            type = boolean.class,
            defaultValue = DEFAULT_PREFERENCE_ENABLED_PROPERTY_VALUE,
            source = APPLICATION_SOURCE
    )
    String PREFERENCE_ENABLED_PROPERTY_NAME = PREFERENCE_PROPERTY_NAME_PREFIX + ENABLED_PROPERTY_NAME;

    // Zone Preference Filter Properties

    /**
     * The property name prefix of zone preference filter : "microsphere.availability.zone.preference.filter."
     */
    String PREFERENCE_FILER_PROPERTY_NAME_PREFIX = PREFERENCE_PROPERTY_NAME_PREFIX + "filter" + DOT;

    /**
     * The property value of default zone preference filter order : "10"
     */
    String DEFAULT_ZONE_PREFERENCE_FILTER_ORDER_PROPERTY_VALUE = "10";

    /**
     * The default value of zone preference filter order.
     */
    int DEFAULT_ZONE_PREFERENCE_FILTER_ORDER = parseInt(DEFAULT_ZONE_PREFERENCE_FILTER_ORDER_PROPERTY_VALUE);

    /**
     * The property name of zone preference filter order : "microsphere.availability.zone.preference.filter.order"
     */
    @ConfigurationProperty(
            type = int.class,
            defaultValue = DEFAULT_ZONE_PREFERENCE_FILTER_ORDER_PROPERTY_VALUE,
            source = APPLICATION_SOURCE
    )
    String PREFERENCE_FILTER_ORDER_PROPERTY_NAME = PREFERENCE_FILER_PROPERTY_NAME_PREFIX + "order";

    // Zone Preference Upstream Properties

    /**
     * The property name prefix of zone preference upstream : "microsphere.availability.zone.preference.upstream."
     */
    String PREFERENCE_UPSTREAM_PROPERTY_NAME_PREFIX = PREFERENCE_PROPERTY_NAME_PREFIX + "upstream" + DOT;

    /**
     * The property value of default zone preference upstream zone ready percentage : "100"
     */
    String DEFAULT_PREFERENCE_UPSTREAM_ZONE_READY_PERCENTAGE_PROPERTY_VALUE = "100";

    /**
     * The default value of zone preference upstream zone ready percentage.
     */
    int DEFAULT_PREFERENCE_UPSTREAM_ZONE_READY_PERCENTAGE = parseInt(DEFAULT_PREFERENCE_UPSTREAM_ZONE_READY_PERCENTAGE_PROPERTY_VALUE);

    /**
     * The property name of zone preference upstream zone ready percentage : "microsphere.availability.zone.preference.upstream.zone-ready-percentage"
     */
    @ConfigurationProperty(
            type = int.class,
            defaultValue = DEFAULT_PREFERENCE_UPSTREAM_ZONE_READY_PERCENTAGE_PROPERTY_VALUE,
            source = APPLICATION_SOURCE
    )
    String PREFERENCE_UPSTREAM_ZONE_READY_PERCENTAGE_PROPERTY_NAME = PREFERENCE_UPSTREAM_PROPERTY_NAME_PREFIX + "zone-ready-percentage";

    // Zone Preference Upstream Same Zone Properties

    /**
     * The property value of default zone preference upstream same zone min available : "5"
     */
    String DEFAULT_PREFERENCE_UPSTREAM_SAME_ZONE_MIN_AVAILABLE_PROPERTY_VALUE = "5";

    /**
     * The default value of zone preference upstream same zone min available.
     */
    int DEFAULT_PREFERENCE_UPSTREAM_SAME_ZONE_MIN_AVAILABLE = parseInt(DEFAULT_PREFERENCE_UPSTREAM_SAME_ZONE_MIN_AVAILABLE_PROPERTY_VALUE);

    /**
     * The property name of zone preference upstream same zone min available : "microsphere.availability.zone.preference.upstream.same-zone-min-available"
     */
    @ConfigurationProperty(
            type = int.class,
            defaultValue = DEFAULT_PREFERENCE_UPSTREAM_SAME_ZONE_MIN_AVAILABLE_PROPERTY_VALUE,
            source = APPLICATION_SOURCE
    )
    String PREFERENCE_UPSTREAM_SAME_ZONE_MIN_AVAILABLE_PROPERTY_NAME = PREFERENCE_UPSTREAM_PROPERTY_NAME_PREFIX + "same-zone-min-available";

    // Zone Preference Upstream Disabled Zone Properties

    /**
     * The property value of default zone preference upstream disabled zone : null
     */
    String DEFAULT_PREFERENCE_UPSTREAM_DISABLED_ZONE = null;

    /**
     * The property name of zone preference upstream disabled zone : "microsphere.availability.zone.preference.upstream.disabled-zone"
     */
    @ConfigurationProperty(
            source = APPLICATION_SOURCE
    )
    String PREFERENCE_UPSTREAM_DISABLED_ZONE_PROPERTY_NAME = PREFERENCE_UPSTREAM_PROPERTY_NAME_PREFIX + "disabled-zone";

    // Zone Locator Properties

    /**
     * The property name prefix of zone locator : "microsphere.availability.zone.locator."
     */
    String LOCATOR_PROPERTY_NAME_PREFIX = ZONE_PROPERTY_NAME + DOT + "locator" + DOT;

    /**
     * The property value of default zone locator fast fail : "false"
     */
    String DEFAULT_LOCATOR_FAST_FAIL_PROPERTY_VALUE = "false";

    /**
     * The default value of zone locator fast fail.
     */
    boolean DEFAULT_LOCATOR_FAST_FAIL = parseBoolean(DEFAULT_LOCATOR_FAST_FAIL_PROPERTY_VALUE);

    /**
     * The property name of zone locator fast fail : "microsphere.availability.zone.locator.fast-fail"
     */
    @ConfigurationProperty(
            type = boolean.class,
            defaultValue = DEFAULT_LOCATOR_FAST_FAIL_PROPERTY_VALUE,
            source = APPLICATION_SOURCE
    )
    String LOCATOR_FAST_FAIL_PROPERTY_NAME = LOCATOR_PROPERTY_NAME_PREFIX + "fast-fail";

    /**
     * The property value of default zone locator timeout : "3000"
     */
    String DEFAULT_TIMEOUT_PROPERTY_VALUE = "3000";

    /**
     * The default value of zone locator timeout.
     */
    int DEFAULT_TIMEOUT = parseInt(DEFAULT_TIMEOUT_PROPERTY_VALUE);

    /**
     * The property name of zone locator timeout : "microsphere.availability.zone.locator.timeout"
     */
    @ConfigurationProperty(
            type = int.class,
            defaultValue = DEFAULT_TIMEOUT_PROPERTY_VALUE,
            source = APPLICATION_SOURCE
    )
    String LOCATOR_TIMEOUT_PROPERTY_NAME = LOCATOR_PROPERTY_NAME_PREFIX + "timeout";

}
