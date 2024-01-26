package io.microsphere.multiple.active.zone.spring.cloud.loadbalancer;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClientSpecification;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * {@link ZonePreferenceServiceInstanceListSupplier} Auto-Configuration Class
 *
 * @author <a href="mailto:warlklown@gmail.com">Walklown<a/>
 * @since 1.0.0
 */
@Component
@ConditionalOnProperty(value = "microsphere.customized-loadbalancer.enabled", havingValue = "true")
public class CustomizedLoadBalancerAutoConfiguration implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!(bean instanceof LoadBalancerClientFactory)) {
            return bean;
        }
        LoadBalancerClientFactory loadBalancerClientFactory = (LoadBalancerClientFactory) bean;
        loadBalancerClientFactory.setConfigurations(Collections.singletonList(
                new LoadBalancerClientSpecification("default.customized-load-balance-client",
                        new Class[]{CustomizedLoadBalancerClientConfiguration.class})));
        return bean;
    }
}
