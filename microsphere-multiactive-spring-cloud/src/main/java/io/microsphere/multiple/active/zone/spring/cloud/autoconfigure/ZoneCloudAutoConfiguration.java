package io.microsphere.multiple.active.zone.spring.cloud.autoconfigure;

import io.microsphere.multiple.active.zone.ZoneAttachmentHandler;
import io.microsphere.multiple.active.zone.ZoneContext;
import io.microsphere.multiple.active.zone.spring.boot.autoconfigure.ZoneAutoConfiguration;
import io.microsphere.multiple.active.zone.spring.boot.condition.ConditionalOnAvailabilityZoneAvailable;
import io.microsphere.multiple.active.zone.spring.cloud.event.ZoneAttachmentListener;
import io.microsphere.spring.cloud.client.service.registry.condition.ConditionalOnAutoServiceRegistrationEnabled;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static io.microsphere.spring.cloud.client.service.registry.constants.ServiceRegistryConstants.REGISTRATION_CLASS_NAME;

/**
 * Zone Auto-Configuration for Spring Cloud
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see io.microsphere.multiple.active.zone.spring.boot.autoconfigure.ZoneAutoConfiguration
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnAvailabilityZoneAvailable
@ConditionalOnAutoServiceRegistrationEnabled
@ConditionalOnClass(name = {
        "org.aspectj.lang.annotation.Aspect",                                                                        // AspectJ API
        "io.microsphere.spring.cloud.client.service.registry.event.RegistrationPreRegisteredEvent",                  // Microsphere Spring Cloud Commons API
        REGISTRATION_CLASS_NAME
})
@ConditionalOnBean(ZoneContext.class)
@AutoConfigureAfter(
        value = {
                ZoneAutoConfiguration.class
        },
        name = {
                "org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration",                            // Spring Cloud Netflix Eureka API
                "io.microsphere.spring.cloud.client.service.registry.autoconfigure.ServiceRegistryAutoConfiguration" // Microsphere Spring Cloud Commons API
        })
@Import(value = {
        ZoneAttachmentHandler.class
})
public class ZoneCloudAutoConfiguration {

    @Bean
    @ConditionalOnBean(type = "io.microsphere.spring.cloud.client.service.registry.aspect.EventPublishingRegistrationAspect")
    @ConditionalOnMissingBean
    public ZoneAttachmentListener zoneAttachmentListener() {
        return new ZoneAttachmentListener();
    }

}