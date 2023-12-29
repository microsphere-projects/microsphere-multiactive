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
package io.microsphere.multiple.active.zone.spring.cloud.netflix.ribbon;

import com.netflix.loadbalancer.Server;
import io.microsphere.multiple.active.zone.cloud.CloudServerZoneResolver;
import org.springframework.cloud.client.ServiceInstance;

import static io.microsphere.multiple.active.zone.cloud.CloudServerZoneResolver.INSTANCE;

/**
 * Ribbon {@link Server} based on Spring Cloud Commons {@link ServiceInstance}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since ServiceInstance
 * @since Server
 */
public class DiscoveryClientServer extends Server {

    private final ServiceInstance serviceInstance;

    private final ServiceInstanceMetaInfo metaInfo;

    public DiscoveryClientServer(ServiceInstance serviceInstance) {
        super(serviceInstance.getScheme(), serviceInstance.getHost(), serviceInstance.getPort());
        this.serviceInstance = serviceInstance;
        this.metaInfo = new ServiceInstanceMetaInfo(serviceInstance);
        this.setZone(resolveZone(serviceInstance));
    }

    private String resolveZone(ServiceInstance serviceInstance) {
        return INSTANCE.resolve(serviceInstance);
    }

    @Override
    public String getId() {
        return serviceInstance.getInstanceId();
    }

    @Override
    public MetaInfo getMetaInfo() {
        return metaInfo;
    }

    final static class ServiceInstanceMetaInfo implements MetaInfo {

        private final ServiceInstance serviceInstance;

        ServiceInstanceMetaInfo(ServiceInstance serviceInstance) {
            this.serviceInstance = serviceInstance;
        }

        @Override
        public String getAppName() {
            return serviceInstance.getServiceId();
        }

        @Override
        public String getServerGroup() {
            return null;
        }

        @Override
        public String getServiceIdForDiscovery() {
            return serviceInstance.getServiceId();
        }

        @Override
        public String getInstanceId() {
            return serviceInstance.getInstanceId();
        }
    }
}
