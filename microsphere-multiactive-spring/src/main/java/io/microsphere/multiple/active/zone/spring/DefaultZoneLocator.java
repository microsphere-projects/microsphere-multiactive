package io.microsphere.multiple.active.zone.spring;

import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import static io.microsphere.multiple.active.zone.ZoneConstants.DEFAULT_ZONE;
import static io.microsphere.multiple.active.zone.ZoneConstants.ZONE_PROPERTY_NAME;

/**
 * The Default {@link ZoneLocator} implementation
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class DefaultZoneLocator extends AbstractZoneLocator {

    public static final int DEFAULT_ORDER = 20;

    public DefaultZoneLocator() {
        super(DEFAULT_ORDER);
    }

    @Override
    public boolean supports(Environment environment) {
        return true;
    }

    @Override
    public String locate(Environment environment) {
        String zone = environment.getProperty(ZONE_PROPERTY_NAME);
        if (StringUtils.hasText(zone)) {
            logger.info("The zone ['{}'] was located from the Spring Property [name: '{}']", zone, ZONE_PROPERTY_NAME);
        } else {
            logger.debug("The property [name : '{}' ] of zone can't be found, the default value ['{}'] will be applied", ZONE_PROPERTY_NAME,
                    DEFAULT_ZONE);
            zone = DEFAULT_ZONE;
        }
        return zone;
    }
}
