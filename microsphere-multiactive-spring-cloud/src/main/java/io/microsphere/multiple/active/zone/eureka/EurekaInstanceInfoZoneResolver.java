package io.microsphere.multiple.active.zone.eureka;

import com.netflix.appinfo.InstanceInfo;
import io.microsphere.multiple.active.zone.ZoneResolver;

import java.util.Map;

import static io.microsphere.multiple.active.zone.ZoneConstants.ZONE_PROPERTY_NAME;

/**
 * {@link ZoneResolver} for Netflix Eureka {@link InstanceInfo}
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class EurekaInstanceInfoZoneResolver implements ZoneResolver<InstanceInfo> {

    @Override
    public String resolve(InstanceInfo instanceInfo) {
        Map<String, String> metadata = instanceInfo.getMetadata();
        String instanceZone = metadata == null ? null : metadata.get(ZONE_PROPERTY_NAME);
        return instanceZone;
    }
}
