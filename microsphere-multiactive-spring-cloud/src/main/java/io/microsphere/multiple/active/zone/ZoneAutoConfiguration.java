package io.microsphere.multiple.active.zone;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static io.microsphere.multiple.active.zone.ZoneConstants.ENABLED_PROPERTY_NAME_SUFFIX;
import static io.microsphere.multiple.active.zone.ZoneConstants.ZONE_PROPERTY_NAME;

/**
 * Zone Auto-Configuration
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
@Configuration
@ConditionalOnProperty(prefix = ZONE_PROPERTY_NAME, name = ENABLED_PROPERTY_NAME_SUFFIX)
@Import(value = {ZoneContextChangedListener.class, ZoneAttachmentHandler.class})
@AutoConfigureAfter(name = {"org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration"})
public class ZoneAutoConfiguration {


    @Bean
    @ConditionalOnClass(name = "org.springframework.cloud.client.discovery.event.InstancePreRegisteredEvent")
    public ZoneAttachmentListener zoneAttachmentListener() {
        return new ZoneAttachmentListener();
    }

}
