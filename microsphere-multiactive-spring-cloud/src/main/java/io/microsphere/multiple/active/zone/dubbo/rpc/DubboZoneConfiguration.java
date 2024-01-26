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
package io.microsphere.multiple.active.zone.dubbo.rpc;

import io.microsphere.multiple.active.zone.ZoneContext;
import io.microsphere.multiple.active.zone.ZonePreferenceFilter;
import io.microsphere.multiple.active.zone.dubbo.rpc.cluster.InvokerZoneResolver;
import io.microsphere.multiple.active.zone.dubbo.rpc.cluster.ZonePreferenceRouter;
import org.apache.dubbo.rpc.Invoker;
import org.springframework.context.annotation.Bean;

/**
 * TODO Comment
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since TODO
 */
public class DubboZoneConfiguration {

    @Bean
    public InvokerZoneResolver invokerZoneResolver() {
        return new InvokerZoneResolver();
    }

    @Bean
    public ZonePreferenceFilter<Invoker> zonePreferenceFilter(ZoneContext zoneContext, InvokerZoneResolver zoneResolver) {
        return new ZonePreferenceFilter(zoneContext, zoneResolver);
    }
}
