/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2020-2021 Agorapulse.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.agorapulse.micronaut.newrelic;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;

class DefaultNewRelicInsightsEvent implements NewRelicInsightsEvent {

    private final Long timestamp = System.currentTimeMillis();

    @NotBlank private final String eventType;
    @Nullable private final String key;
    @Nullable private final Long value;

    public DefaultNewRelicInsightsEvent(String eventType) {
        this(eventType, null, 1L);
    }

    public DefaultNewRelicInsightsEvent(String eventType, String key, Long value) {
        this.eventType = eventType;
        this.key = key;
        this.value = value;
    }

    @Override
    public String getEventType() {
        return eventType;
    }

    public String getKey() {
        return key;
    }

    @Override
    public Long getTimestamp() {
        return timestamp;
    }

    public Long getValue() {
        return value;
    }

}
