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

import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.core.beans.BeanIntrospector;

import jakarta.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Singleton
public class BeanIntrospectionEventPayloadExtractor implements EventPayloadExtractor {

    private final BeanIntrospector introspector = BeanIntrospector.SHARED;

    @Override
    @SuppressWarnings("unchecked")
    public <E> Map<String, Object> extractPayload(E event) {
        BeanIntrospection<E> introspection = (BeanIntrospection<E>) introspector
            .findIntrospection(event.getClass())
            .orElseThrow(() ->
                //should never happen ad the core event is annotated with @Introspected
                new IllegalArgumentException("Cannot find introspection for class " + event.getClass() + ". Please, annotate the class with @Introspected")
            );

        String[] propertyNames = introspection.getPropertyNames();
        Map<String, Object> map = new HashMap<>(propertyNames.length - 1);

        for (String name : propertyNames) {
            introspection.getProperty(name)
                .flatMap(p -> Optional.ofNullable(p.get(event)))
                .map(v -> (v instanceof Boolean || v instanceof Number) ? v : String.valueOf(v))
                .ifPresent(v -> map.put(name, v));
        }

        map.computeIfAbsent("eventType", k -> introspection.getBeanType().getSimpleName());
        map.computeIfAbsent("timestamp", k -> System.currentTimeMillis());
        return map;
    }

}
