package io.microsphere.multiple.active.zone.aws;

import io.microsphere.multiple.active.zone.AbstractZoneLocator;
import io.microsphere.multiple.active.zone.ZoneLocator;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/**
 * Amazon EC2 Availability Zone Endpoint {@link ZoneLocator}
 *
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see ZoneLocator
 * @since 1.0.0
 */
public class Ec2AvailabilityZoneEndpointZoneLocator extends AbstractZoneLocator {

    public static final String AVAILABILITY_ZONE_ENDPOINT_URI_PROPERTY_NAME = "EC2_AVAILABILITY_ZONE_ENDPOINT_URI";

    public static final String DEFAULT_AVAILABILITY_ZONE_ENDPOINT_URI = "http://169.254.169.254/latest/meta-data/placement/availability-zone";

    public static final int DEFAULT_ORDER = 15;

    public Ec2AvailabilityZoneEndpointZoneLocator() {
        super(DEFAULT_ORDER);
    }

    @Override
    public boolean supports(Environment environment) {
        return true;
    }

    @Override
    public String locate(Environment environment) {
        String uri = getAvailabilityZoneEndpointURI(environment);
        String zone = null;
        if (StringUtils.hasText(uri)) {
            try {
                zone = doGet(uri);
                logger.info("The zone ['{}'] was located from the EC2 Availability Zone Endpoint[URI : '{}' , property name: '{}']", zone, uri,
                        AVAILABILITY_ZONE_ENDPOINT_URI_PROPERTY_NAME);
            } catch (Throwable e) {
                logger.error("Request Amazon EC2 Availability Zone Endpoint[URI : '{}'] failed", uri, e);
            }
        } else {
            logger.warn("The property[name : '{}'] of Amazon EC2 Availability Zone Endpoint can't be found",
                    AVAILABILITY_ZONE_ENDPOINT_URI_PROPERTY_NAME);
        }
        return zone;
    }

    private String getAvailabilityZoneEndpointURI(Environment environment) {
        return environment.getProperty(AVAILABILITY_ZONE_ENDPOINT_URI_PROPERTY_NAME, DEFAULT_AVAILABILITY_ZONE_ENDPOINT_URI);
    }
}
