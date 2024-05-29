/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2022-2024 Agorapulse.
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

import io.micronaut.core.annotation.Introspected;

import io.micronaut.core.beans.BeanIntrospector;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Optinal interface describing a New Relic NRQL Event. Classes extending it will automatically transformed into JSON when sent
 * to the New Relic API by the Micronaut HTTP client.
 */
@Introspected
public interface NewRelicInsightsEvent {

    static NewRelicInsightsEvent create(String eventType) {
        return new DefaultNewRelicInsightsEvent(eventType);
    }

    static NewRelicInsightsEvent create(String eventType, String key, Long value) {
        return new DefaultNewRelicInsightsEvent(eventType, key, value);
    }

    @NotBlank
    default String getEventType() {
        return getClass().getSimpleName();
    }

    @NotNull
    default Long getTimestamp() {
        return System.currentTimeMillis();
    }

    default boolean isCritical() {
        return BeanIntrospector.SHARED.findIntrospection(getClass()).map(i -> i.findAnnotation(Critical.class).isPresent()).orElse(false);
    }

}
