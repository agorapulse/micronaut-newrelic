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

import java.util.Map;

public interface EventPayloadExtractor {

    static boolean isCritical(Map<String, Object> payload) {
        return payload.containsKey("critical") && Boolean.TRUE.equals(payload.get("critical"));
    }

    static boolean isNonCritical(Map<String, Object> payload) {
        return !isCritical(payload);
    }

    static void removeCritical(Map<String, Object> event) {
        event.remove("critical");
    }

    /**
     * Extracts the paylaod for the event.
     *
     * The payload always contains keys <code>eventType</code> and <code>timestamp</code>
     * @param event the event object
     * @param <E> the type of the event
     * @return payload map which always contains the keys <code>eventType</code> and <code>timestamp</code>
     */
    <E> Map<String, Object> extractPayload(E event);

}
