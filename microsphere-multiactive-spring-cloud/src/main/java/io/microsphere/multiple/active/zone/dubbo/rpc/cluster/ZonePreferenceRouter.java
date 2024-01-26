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
package io.microsphere.multiple.active.zone.dubbo.rpc.cluster;

import io.microsphere.multiple.active.zone.ZonePreferenceFilter;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionFactory;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.Router;
import org.apache.dubbo.rpc.cluster.router.AbstractRouter;

import java.util.List;

import static org.apache.dubbo.common.extension.ExtensionLoader.getExtensionLoader;

/**
 * Apache Dubbo {@link Router} based on {@link ZonePreferenceFilter Zone Preference}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ZonePreferenceFilter
 * @since 1.0.0
 */
public class ZonePreferenceRouter extends AbstractRouter {

    private volatile ZonePreferenceFilter<Invoker<?>> zonePreferenceFilter;
    private ExtensionFactory extensionFactory = getExtensionLoader(ExtensionFactory.class)
            .getExtension("spring");

    public ZonePreferenceRouter(URL url) {
        super(url);
    }

    @Override
    public <T> List<Invoker<T>> route(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
        ZonePreferenceFilter<Invoker<?>> zonePreferenceFilter = getZonePreferenceFilter();
        List filteredLists = invokers;
        filteredLists = zonePreferenceFilter.filter(filteredLists);
        return filteredLists;
    }

    private ZonePreferenceFilter<Invoker<?>> getZonePreferenceFilter() {
        ZonePreferenceFilter<Invoker<?>> zonePreferenceFilter = this.zonePreferenceFilter;
        if (zonePreferenceFilter == null) {
            zonePreferenceFilter = extensionFactory.getExtension(ZonePreferenceFilter.class, "zonePreferenceFilter");
        }
        this.zonePreferenceFilter = zonePreferenceFilter;
        return zonePreferenceFilter;
    }
}
