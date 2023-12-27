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
package io.microsphere.multiple.active.zone.ribbon;

import com.netflix.loadbalancer.ServerList;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link ServerList} based on Spring Cloud {@link DiscoveryClient}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see DiscoveryClientServer
 * @see ServerList
 * @since DiscoveryClient
 */
public class DiscoveryClientServerList implements ServerList<DiscoveryClientServer> {

    private final DiscoveryClient discoveryClient;

    private final String serviceName;

    public DiscoveryClientServerList(DiscoveryClient discoveryClient, String serviceName) {
        this.discoveryClient = discoveryClient;
        this.serviceName = serviceName;
    }

    @Override
    public List<DiscoveryClientServer> getInitialListOfServers() {
        return getUpdatedListOfServers();
    }

    @Override
    public List<DiscoveryClientServer> getUpdatedListOfServers() {
        return discoveryClient.getInstances(serviceName)
                .stream()
                .map(DiscoveryClientServer::new)
                .collect(Collectors.toList());
    }
}
