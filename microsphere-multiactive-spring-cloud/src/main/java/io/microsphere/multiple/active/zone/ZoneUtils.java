package io.microsphere.multiple.active.zone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import static io.microsphere.multiple.active.zone.ZoneConstants.ZONE_CONTEXT_BEAN_NAME;
import static io.microsphere.multiple.active.zone.ZoneConstants.ZONE_LOCATOR_BEAN_NAME;

/**
 * The utilities class for Zone
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public abstract class ZoneUtils {

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
