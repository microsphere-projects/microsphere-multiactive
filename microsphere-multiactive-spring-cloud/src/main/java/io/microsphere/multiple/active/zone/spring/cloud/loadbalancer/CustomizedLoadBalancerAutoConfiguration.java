package io.microsphere.multiple.active.zone.spring.cloud.loadbalancer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.context.annotation.Configuration;

/**
 * {@link ZonePreferenceServiceInstanceListSupplier} Auto-Configuration Class
 *
 * @author <a href="mailto:warlklown@gmail.com">Walklown<a/>
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(value = "microsphere.spring.cloud.loadbalancer.customized", havingValue = "true")
@ConditionalOnClass(name = {
        "org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient"
})
@LoadBalancerClients(defaultConfiguration = CustomizedLoadBalancerClientConfiguration.class)
public class CustomizedLoadBalancerAutoConfiguration {
}
