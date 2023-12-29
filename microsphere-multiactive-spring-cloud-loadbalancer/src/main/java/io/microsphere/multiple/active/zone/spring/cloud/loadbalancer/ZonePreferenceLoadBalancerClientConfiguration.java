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

import io.microsphere.multiple.active.zone.ZonePreferenceFilter;
import org.springframework.cloud.client.ConditionalOnBlockingDiscoveryEnabled;
import org.springframework.cloud.client.ConditionalOnReactiveDiscoveryEnabled;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClientConfiguration;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

/**
 * Zone Preference {@link LoadBalancerClient} {@link Configuration}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see Configuration
 * @see ZonePreferenceServiceInstanceListSupplier
 * @see LoadBalancerClient
 * @see LoadBalancerClientConfiguration
 * @since 1.0.0
 */
@Import(value = {
        ZonePreferenceLoadBalancerClientConfiguration.ReactiveConfiguration.class,
        ZonePreferenceLoadBalancerClientConfiguration.BlockingConfiguration.class,
})
public class ZonePreferenceLoadBalancerClientConfiguration {

    @ConditionalOnReactiveDiscoveryEnabled
    @Order(193827463)
    static class ReactiveConfiguration {

        @Bean
        public ServiceInstanceListSupplier zonePreferenceServiceInstanceListSupplier(
                ConfigurableApplicationContext context,
                ZonePreferenceFilter<ServiceInstance> zonePreferenceFilter) {
            return ServiceInstanceListSupplier.builder().withDiscoveryClient().with(
                    (ctx, delegate) -> new ZonePreferenceServiceInstanceListSupplier(delegate, zonePreferenceFilter)
            ).build(context);
        }
    }


    @ConditionalOnBlockingDiscoveryEnabled
    @Order(193827464)
    static class BlockingConfiguration {

        @Bean
        public ServiceInstanceListSupplier zonePreferenceServiceInstanceListSupplier(
                ConfigurableApplicationContext context,
                ZonePreferenceFilter<ServiceInstance> zonePreferenceFilter) {
            return ServiceInstanceListSupplier.builder().withBlockingDiscoveryClient().with(
                    (ctx, delegate) -> new ZonePreferenceServiceInstanceListSupplier(delegate, zonePreferenceFilter)
            ).build(context);
        }
    }


}
