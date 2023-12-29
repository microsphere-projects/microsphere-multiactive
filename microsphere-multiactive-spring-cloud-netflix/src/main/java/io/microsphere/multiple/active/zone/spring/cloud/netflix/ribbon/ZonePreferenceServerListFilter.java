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
import com.netflix.loadbalancer.ServerListFilter;
import io.microsphere.multiple.active.zone.ZonePreferenceFilter;

import java.util.List;

/**
 * {@link ZonePreferenceFilter} integrates {@link ServerListFilter}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ZonePreferenceFilter
 * @see ServerListFilter
 * @since 1.0.0
 */
public class ZonePreferenceServerListFilter<T extends Server> implements ServerListFilter<T> {

    private ZonePreferenceFilter<T> filter;

    public ZonePreferenceServerListFilter(ZonePreferenceFilter<T> filter) {
        this.filter = filter;
    }

    @Override
    public List<T> getFilteredListOfServers(List<T> servers) {
        return filter.filter(servers);
    }
}
