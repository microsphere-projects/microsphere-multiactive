package io.microsphere.multiple.active.zone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static io.microsphere.multiple.active.zone.ZoneConstants.ZONE_PROPERTY_NAME;
import static io.microsphere.util.StringUtils.isNotBlank;

/**
 * The handler to attach zone
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see ZoneContext
 * @since 1.0.0
 */
public class ZoneAttachmentHandler {

    private static final Logger logger = LoggerFactory.getLogger(ZoneAttachmentHandler.class);

    private final ZoneContext zoneContext;

    public ZoneAttachmentHandler(ZoneContext zoneContext) {
        this.zoneContext = zoneContext;
    }

    public void attachZone(Map<String, String> metadata) {
        String zone = zoneContext.getZone();
        if (isNotBlank(zone)) {
            String propertyName = ZONE_PROPERTY_NAME;
            // If metadata is unmodifiable, UnsupportedOperationException will be thrown.
            try {
                metadata.put(propertyName, zone);
                logger.info("The zone ['{}'] has been attached into meta-data [name : '{}']", zone, propertyName);
            } catch (Throwable e) {
                logger.warn("The zone ['{}'] can't be attached into meta-data [name : '{}']", zone, propertyName);
            }
        } else {
            logger.warn("No zone info can't be found in the context!");
        }
    }
}
