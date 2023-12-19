package io.microsphere.multiple.active.zone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static io.microsphere.multiple.active.zone.ZoneConstants.CURRENT_ZONE_PROPERTY_NAME;
import static io.microsphere.multiple.active.zone.ZoneConstants.DEFAULT_LOCATOR_FAST_FAIL;
import static io.microsphere.multiple.active.zone.ZoneConstants.LOCATOR_FAST_FAIL_PROPERTY_NAME;

/**
 * The composition of {@link ZoneLocator} list.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class CompositeZoneLocator implements ZoneLocator {

    private static final Logger logger = LoggerFactory.getLogger(CompositeZoneLocator.class);

    private final List<ZoneLocator> zoneLocators;

    private volatile String zone;

    public CompositeZoneLocator(List<ZoneLocator> zoneLocators) {
        Assert.notNull(zoneLocators, "The argument 'zoneLocators' must not be null!");
        this.zoneLocators = new ArrayList<>(zoneLocators);
        AnnotationAwareOrderComparator.sort(this.zoneLocators);
    }

    @Override
    public boolean supports(Environment environment) {
        // Supported when at least one object supports.
        for (ZoneLocator zoneLocator : zoneLocators) {
            if (zoneLocator.supports(environment)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String locate(Environment environment) {
        // return the located zone at the first time
        if (StringUtils.hasText(zone)) {
            return zone;
        }

        String zone = null;

        boolean fastFailEnabled = isFastFailEnabled(environment);

        ZoneLocator failedZoneLocator = null;

        for (ZoneLocator zoneLocator : zoneLocators) {
            try {
                if (zoneLocator.supports(environment)) {
                    zone = zoneLocator.locate(environment);
                    if (zone != null) {
                        logger.info("{} locates the zone : {}", zoneLocator, zone);
                        break;
                    } else {
                        logger.warn("{} can't locate the zone", zoneLocator);
                        if (fastFailEnabled) {
                            failedZoneLocator = zoneLocator;
                            break;
                        }
                    }
                } else {
                    logger.debug("{} does not support to locate the zone", zoneLocator);
                }
            } catch (Throwable e) {
                logger.error("{} failed to locate the zone", zoneLocator, e);
                if (fastFailEnabled) {
                    failedZoneLocator = zoneLocator;
                    break;
                }
            }
        }

        if (failedZoneLocator != null) {
            throw new IllegalStateException("The zone can't be located by " + failedZoneLocator);
        }

        if (zone == null) {
            logger.warn("The zone can't be located by anyone of zoneLocators : {}", zoneLocators);
        }

        this.zone = zone;

        System.setProperty(CURRENT_ZONE_PROPERTY_NAME, this.zone);

        return zone;
    }

    private boolean isFastFailEnabled(Environment environment) {
        return environment.getProperty(LOCATOR_FAST_FAIL_PROPERTY_NAME, boolean.class, DEFAULT_LOCATOR_FAST_FAIL);
    }

    @Override
    public String toString() {
        return "CompositeZoneLocator{" + "zoneLocators=" + zoneLocators + ", zone=" + zone + '}';
    }
}
