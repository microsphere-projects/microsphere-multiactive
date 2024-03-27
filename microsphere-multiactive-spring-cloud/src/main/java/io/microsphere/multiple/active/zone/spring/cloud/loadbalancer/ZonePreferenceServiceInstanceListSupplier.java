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
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.DelegatingServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * Optimized {@link org.springframework.cloud.loadbalancer.core.ZonePreferenceServiceInstanceListSupplier}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ZonePreferenceFilter
 * @since org.springframework.cloud.loadbalancer.core.ZonePreferenceServiceInstanceListSupplier
 */
public class ZonePreferenceServiceInstanceListSupplier extends DelegatingServiceInstanceListSupplier {

    private final ZonePreferenceFilter<ServiceInstance> zonePreferenceFilter;

    public ZonePreferenceServiceInstanceListSupplier(ServiceInstanceListSupplier delegate,
                                                     ZonePreferenceFilter<ServiceInstance> zonePreferenceFilter) {
        super(delegate);
        this.zonePreferenceFilter = zonePreferenceFilter;
    }

    @Override
    public Flux<List<ServiceInstance>> get() {
        return getDelegate().get().map(this::filteredByZone);
    }

    private List<ServiceInstance> filteredByZone(List<ServiceInstance> serviceInstances) {
        return zonePreferenceFilter.filter(serviceInstances);
    }
}
