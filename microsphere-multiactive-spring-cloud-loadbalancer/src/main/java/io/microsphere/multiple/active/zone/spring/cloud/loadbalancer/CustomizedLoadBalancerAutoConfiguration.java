package io.microsphere.multiple.active.zone.spring.cloud.loadbalancer;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClientSpecification;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

/**
 * {@link ZonePreferenceServiceInstanceListSupplier} Auto-Configuration Class
 *
 * @author <a href="mailto:warlklown@gmail.com">Walklown<a/>
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(LoadBalancerClientFactory.class)
@ConditionalOnProperty(value = "microsphere.customized-loadbalancer.enabled", havingValue = "true")
public class CustomizedLoadBalancerAutoConfiguration implements InitializingBean {

    @Autowired
    private LoadBalancerClientFactory loadBalancerClientFactory;

    @Override
    public void afterPropertiesSet() throws Exception {
        loadBalancerClientFactory.setConfigurations(Collections.singletonList(
                new LoadBalancerClientSpecification("default.my-http-client",
                        new Class[]{CustomizedLoadBalancerClientConfiguration.class})));
    }
}
