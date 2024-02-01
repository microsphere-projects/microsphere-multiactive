package io.microsphere.multiple.active.zone.spring.cloud.autoconfigure;

import io.microsphere.multiple.active.zone.ZoneAttachmentHandler;
import io.microsphere.multiple.active.zone.spring.boot.condition.ConditionalOnEnabledZone;
import io.microsphere.multiple.active.zone.spring.cloud.event.ZoneAttachmentListener;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Zone Auto-Configuration for Spring Cloud
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see io.microsphere.multiple.active.zone.spring.boot.autoconfigure.ZoneAutoConfiguration
 * @since 1.0.0
 */
@Configuration
@ConditionalOnEnabledZone
@Import(value = {ZoneAttachmentHandler.class})
@AutoConfigureAfter(name = {"org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration"})
public class ZoneAutoConfiguration {


    @Bean
    @ConditionalOnClass(name = "org.springframework.cloud.client.discovery.event.InstancePreRegisteredEvent")
    public ZoneAttachmentListener zoneAttachmentListener() {
        return new ZoneAttachmentListener();
    }

}
