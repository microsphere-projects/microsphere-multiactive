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

package io.microsphere.multiple.active.zone;


import org.junit.jupiter.api.Test;

import static io.microsphere.multiple.active.zone.ZoneConstants.CURRENT_ZONE_PROPERTY_NAME;
import static io.microsphere.multiple.active.zone.ZoneConstants.DEFAULT_LOCATOR_FAST_FAIL;
import static io.microsphere.multiple.active.zone.ZoneConstants.DEFAULT_LOCATOR_FAST_FAIL_PROPERTY_VALUE;
import static io.microsphere.multiple.active.zone.ZoneConstants.DEFAULT_PREFERENCE_ENABLED_PROPERTY_VALUE;
import static io.microsphere.multiple.active.zone.ZoneConstants.DEFAULT_PREFERENCE_UPSTREAM_DISABLED_ZONE;
import static io.microsphere.multiple.active.zone.ZoneConstants.DEFAULT_PREFERENCE_UPSTREAM_SAME_ZONE_MIN_AVAILABLE;
import static io.microsphere.multiple.active.zone.ZoneConstants.DEFAULT_PREFERENCE_UPSTREAM_SAME_ZONE_MIN_AVAILABLE_PROPERTY_VALUE;
import static io.microsphere.multiple.active.zone.ZoneConstants.DEFAULT_PREFERENCE_UPSTREAM_ZONE_READY_PERCENTAGE;
import static io.microsphere.multiple.active.zone.ZoneConstants.DEFAULT_PREFERENCE_UPSTREAM_ZONE_READY_PERCENTAGE_PROPERTY_VALUE;
import static io.microsphere.multiple.active.zone.ZoneConstants.DEFAULT_TIMEOUT;
import static io.microsphere.multiple.active.zone.ZoneConstants.DEFAULT_TIMEOUT_PROPERTY_VALUE;
import static io.microsphere.multiple.active.zone.ZoneConstants.DEFAULT_ZONE;
import static io.microsphere.multiple.active.zone.ZoneConstants.DEFAULT_ZONE_ENABLED;
import static io.microsphere.multiple.active.zone.ZoneConstants.DEFAULT_ZONE_ENABLED_PROPERTY_VALUE;
import static io.microsphere.multiple.active.zone.ZoneConstants.DEFAULT_ZONE_PREFERENCE_ENABLED;
import static io.microsphere.multiple.active.zone.ZoneConstants.DEFAULT_ZONE_PREFERENCE_FILTER_ORDER;
import static io.microsphere.multiple.active.zone.ZoneConstants.DEFAULT_ZONE_PREFERENCE_FILTER_ORDER_PROPERTY_VALUE;
import static io.microsphere.multiple.active.zone.ZoneConstants.ENABLED_PROPERTY_NAME_SUFFIX;
import static io.microsphere.multiple.active.zone.ZoneConstants.LOCATOR_FAST_FAIL_PROPERTY_NAME;
import static io.microsphere.multiple.active.zone.ZoneConstants.LOCATOR_PROPERTY_NAME_PREFIX;
import static io.microsphere.multiple.active.zone.ZoneConstants.LOCATOR_TIMEOUT_PROPERTY_NAME;
import static io.microsphere.multiple.active.zone.ZoneConstants.ORIGINAL_ZONE;
import static io.microsphere.multiple.active.zone.ZoneConstants.PREFERENCE_ENABLED_PROPERTY_NAME;
import static io.microsphere.multiple.active.zone.ZoneConstants.PREFERENCE_FILER_PROPERTY_NAME_PREFIX;
import static io.microsphere.multiple.active.zone.ZoneConstants.PREFERENCE_FILTER_ORDER_PROPERTY_NAME;
import static io.microsphere.multiple.active.zone.ZoneConstants.PREFERENCE_PROPERTY_NAME_PREFIX;
import static io.microsphere.multiple.active.zone.ZoneConstants.PREFERENCE_UPSTREAM_DISABLED_ZONE_PROPERTY_NAME;
import static io.microsphere.multiple.active.zone.ZoneConstants.PREFERENCE_UPSTREAM_PROPERTY_NAME_PREFIX;
import static io.microsphere.multiple.active.zone.ZoneConstants.PREFERENCE_UPSTREAM_SAME_ZONE_MIN_AVAILABLE_PROPERTY_NAME;
import static io.microsphere.multiple.active.zone.ZoneConstants.PREFERENCE_UPSTREAM_ZONE_READY_PERCENTAGE_PROPERTY_NAME;
import static io.microsphere.multiple.active.zone.ZoneConstants.ZONE_ENABLED_PROPERTY_NAME;
import static io.microsphere.multiple.active.zone.ZoneConstants.ZONE_PROPERTY_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link ZoneConstants} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ZoneConstants
 * @since 1.0.0
 */
class ZoneConstantsTest {

    @Test
    void test() {
        // Zone Properties
        assertEquals(".enabled", ENABLED_PROPERTY_NAME_SUFFIX);
        assertEquals("defaultZone", DEFAULT_ZONE);
        assertEquals("originalZone", ORIGINAL_ZONE);
        assertEquals("microsphere.availability.zone", ZONE_PROPERTY_NAME);
        assertEquals("microsphere.current.availability.zone", CURRENT_ZONE_PROPERTY_NAME);
        assertEquals("true", DEFAULT_ZONE_ENABLED_PROPERTY_VALUE);
        assertTrue(DEFAULT_ZONE_ENABLED);
        assertEquals("microsphere.availability.zone.enabled", ZONE_ENABLED_PROPERTY_NAME);

        // Zone Preference Properties
        assertEquals("microsphere.availability.zone.preference.", PREFERENCE_PROPERTY_NAME_PREFIX);
        assertEquals("false", DEFAULT_PREFERENCE_ENABLED_PROPERTY_VALUE);
        assertFalse(DEFAULT_ZONE_PREFERENCE_ENABLED);
        assertEquals("microsphere.availability.zone.preference.enabled", PREFERENCE_ENABLED_PROPERTY_NAME);

        // Zone Preference Filter Properties
        assertEquals("microsphere.availability.zone.preference.filter.", PREFERENCE_FILER_PROPERTY_NAME_PREFIX);
        assertEquals("10", DEFAULT_ZONE_PREFERENCE_FILTER_ORDER_PROPERTY_VALUE);
        assertEquals(10, DEFAULT_ZONE_PREFERENCE_FILTER_ORDER);
        assertEquals("microsphere.availability.zone.preference.filter.order", PREFERENCE_FILTER_ORDER_PROPERTY_NAME);

        // Zone Preference Upstream Properties
        assertEquals("microsphere.availability.zone.preference.upstream.", PREFERENCE_UPSTREAM_PROPERTY_NAME_PREFIX);
        assertEquals("100", DEFAULT_PREFERENCE_UPSTREAM_ZONE_READY_PERCENTAGE_PROPERTY_VALUE);
        assertEquals(100, DEFAULT_PREFERENCE_UPSTREAM_ZONE_READY_PERCENTAGE);
        assertEquals("microsphere.availability.zone.preference.upstream.zone-ready-percentage", PREFERENCE_UPSTREAM_ZONE_READY_PERCENTAGE_PROPERTY_NAME);

        // Zone Preference Upstream Same Zone Properties
        assertEquals("5", DEFAULT_PREFERENCE_UPSTREAM_SAME_ZONE_MIN_AVAILABLE_PROPERTY_VALUE);
        assertEquals(5, DEFAULT_PREFERENCE_UPSTREAM_SAME_ZONE_MIN_AVAILABLE);
        assertEquals("microsphere.availability.zone.preference.upstream.same-zone-min-available", PREFERENCE_UPSTREAM_SAME_ZONE_MIN_AVAILABLE_PROPERTY_NAME);

        // Zone Preference Upstream Disabled Zone Properties
        assertNull(DEFAULT_PREFERENCE_UPSTREAM_DISABLED_ZONE);
        assertEquals("microsphere.availability.zone.preference.upstream.disabled-zone", PREFERENCE_UPSTREAM_DISABLED_ZONE_PROPERTY_NAME);

        // Zone Locator Properties
        assertEquals("microsphere.availability.zone.locator.", LOCATOR_PROPERTY_NAME_PREFIX);
        assertEquals("false", DEFAULT_LOCATOR_FAST_FAIL_PROPERTY_VALUE);
        assertFalse(DEFAULT_LOCATOR_FAST_FAIL);
        assertEquals("microsphere.availability.zone.locator.fast-fail", LOCATOR_FAST_FAIL_PROPERTY_NAME);
        assertEquals("3000", DEFAULT_TIMEOUT_PROPERTY_VALUE);
        assertEquals(3000, DEFAULT_TIMEOUT);
        assertEquals("microsphere.availability.zone.locator.timeout", LOCATOR_TIMEOUT_PROPERTY_NAME);
    }
}