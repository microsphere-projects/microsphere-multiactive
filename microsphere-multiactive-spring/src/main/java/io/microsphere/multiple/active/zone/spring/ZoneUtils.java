package io.microsphere.multiple.active.zone.spring;

import io.microsphere.multiple.active.zone.ZoneContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * The utilities class for Zone
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public abstract class ZoneUtils {

    /**
     * {@link ZoneContext} Bean Name
     */
    public static final String ZONE_CONTEXT_BEAN_NAME = "zoneContext";

    /**
     * {@link ZoneLocator} Bean Name
     */
    public static final String ZONE_LOCATOR_BEAN_NAME = "zoneLocator";


    private static final Logger logger = LoggerFactory.getLogger(ZoneUtils.class);

    private ZoneUtils() {
    }

    public static ZoneContext getZoneContext(ConfigurableListableBeanFactory beanFactory) {
        return beanFactory.getBean(ZONE_CONTEXT_BEAN_NAME, ZoneContext.class);
    }

    public static ZoneLocator getZoneLocator(ConfigurableListableBeanFactory beanFactory) {
        return beanFactory.getBean(ZONE_LOCATOR_BEAN_NAME, ZoneLocator.class);
    }
}
