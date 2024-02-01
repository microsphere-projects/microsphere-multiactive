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
package io.microsphere.multiple.active.zone.spring.boot.condition;

import io.microsphere.multiple.active.zone.ZoneContext;
import io.microsphere.multiple.active.zone.spring.ZoneLocator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static io.microsphere.multiple.active.zone.ZoneConstants.ENABLED_PROPERTY_NAME_SUFFIX;
import static io.microsphere.multiple.active.zone.ZoneConstants.ZONE_PROPERTY_NAME;

/**
 * {@link @Conditional} that checks if zone is enabled
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 *
 * @see Conditional
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@ConditionalOnClass(value = {
        ZoneContext.class, // from "microsphere-multiactive-commons"
        ZoneLocator.class, // from microsphere-multiactive-spring
})
@ConditionalOnProperty(prefix = ZONE_PROPERTY_NAME, name = ENABLED_PROPERTY_NAME_SUFFIX)
public @interface ConditionalOnEnabledZone {
}
