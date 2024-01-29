/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.microsphere.multiple.active.zone.spring.cloud.loadbalancer;

import io.microsphere.multiple.active.zone.ZoneContext;
import io.microsphere.multiple.active.zone.ZonePreferenceFilter;
import io.microsphere.multiple.active.zone.cloud.CloudServerZoneResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.ConditionalOnBlockingDiscoveryEnabled;
import org.springframework.cloud.client.ConditionalOnReactiveDiscoveryEnabled;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClientConfiguration;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerEnvironmentPropertyUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Customized {@link LoadBalancerClients}. Provides some customized implementations of {@link ServiceInstanceListSupplier}.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see Configuration
 * @see ZonePreferenceServiceInstanceListSupplier
 * @see LoadBalancerClients
 * @see LoadBalancerClientConfiguration
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
public class CustomizedLoadBalancerClientConfiguration {

    @ConditionalOnReactiveDiscoveryEnabled
    @Order(193827465)
    static class ReactiveConfiguration {

        @Bean
        @ConditionalOnBean(DiscoveryClient.class)
        @ConditionalOnMissingBean
        @Conditional(OptimizedZoneConfigurationCondition.class)
        public ServiceInstanceListSupplier optimizedZonePreferenceDiscoveryClientServiceInstanceListSupplier(
                ConfigurableApplicationContext context, ZonePreferenceFilter<ServiceInstance> zonePreferenceFilter) {
            return ServiceInstanceListSupplier.builder().withDiscoveryClient().withCaching()
                    .with((ctx, delegate) ->
                            new ZonePreferenceServiceInstanceListSupplier(delegate, zonePreferenceFilter))
                    .build(context);
        }
    }


    @ConditionalOnBlockingDiscoveryEnabled
    @Order(193827466)
    static class BlockingConfiguration {

        @Bean
        @ConditionalOnBean(DiscoveryClient.class)
        @ConditionalOnMissingBean
        @Conditional(OptimizedZoneConfigurationCondition.class)
        public ServiceInstanceListSupplier optimizedZonePreferenceServiceInstanceListSupplier(
                ConfigurableApplicationContext context, ZonePreferenceFilter<ServiceInstance> zonePreferenceFilter) {
            return ServiceInstanceListSupplier.builder().withBlockingDiscoveryClient().withCaching()
                    .with((ctx, delegate) ->
                            new ZonePreferenceServiceInstanceListSupplier(delegate, zonePreferenceFilter))
                    .build(context);
        }
    }

    @Bean
    public ZonePreferenceFilter<ServiceInstance> zonePreferenceFilter(ZoneContext zoneContext) {
        return new ZonePreferenceFilter<ServiceInstance>(zoneContext, new CloudServerZoneResolver());
    }


    static class OptimizedZoneConfigurationCondition implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return LoadBalancerEnvironmentPropertyUtils.equalToForClientOrDefault(context.getEnvironment(),
                    "configurations", "optimized-zone-preference");
        }

    }
}
