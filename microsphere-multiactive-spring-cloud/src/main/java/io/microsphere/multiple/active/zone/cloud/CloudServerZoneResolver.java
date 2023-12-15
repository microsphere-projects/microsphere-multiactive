package io.microsphere.multiple.active.zone.cloud;

import io.microsphere.multiple.active.zone.ZoneResolver;
import org.springframework.cloud.client.ServiceInstance;

import java.util.Map;

import static io.microsphere.multiple.active.zone.ZoneConstants.ZONE_PROPERTY_NAME;

/**
 * {@link ZoneResolver} for Spring cloud {@link ServiceInstance}
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class CloudServerZoneResolver implements ZoneResolver<ServiceInstance> {

    @Override
    public String resolve(ServiceInstance serviceInstance) {
        Map<String, String> instanceMetadata = serviceInstance.getMetadata();
        if (instanceMetadata != null) {
            return instanceMetadata.get(ZONE_PROPERTY_NAME);
        }
        return null;
    }
}
