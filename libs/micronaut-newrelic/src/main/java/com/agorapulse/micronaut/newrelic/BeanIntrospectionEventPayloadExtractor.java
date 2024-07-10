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
            introspection.getProperty(name).ifPresent(property -> {
                boolean isFlatten = property.getAnnotationMetadata().isAnnotationPresent(Flatten.class.getName());
                if (!isFlatten) {
                    map.put(name, getValueWithSupportedType(property.get(event)));
                } else {
                    setFlattenProperties(property.get(event), map);
                }
            });
        }

        map.computeIfAbsent("eventType", k -> introspection.getBeanType().getSimpleName());
        map.computeIfAbsent("timestamp", k -> System.currentTimeMillis());
        map.computeIfAbsent("critical", k -> introspection.findAnnotation(Critical.class).isPresent());
        return map;
    }

    private static void setFlattenProperties(Object value, Map<String, Object> map) {
        if (value == null) {
            return;
        }
        if (!(value instanceof Map additionalData)) {
            throw new IllegalArgumentException("@Flatten annotated getter must return Map<String, Object> but found " + value);
        }
        if (additionalData.keySet().stream().anyMatch(key -> !(key instanceof String))) {
            throw new IllegalArgumentException("@Flatten annotated getter must return Map<String, Object> but found a non String key in " + additionalData);
        }
        Map<String, Object> formattedAdditionalData = new HashMap<>(additionalData);
        formattedAdditionalData.replaceAll((k, v) -> getValueWithSupportedType(v));
        map.putAll(formattedAdditionalData);
    }

    private static Object getValueWithSupportedType(Object value) {
        if (value == null) {
            return null;
        }
        return value instanceof Boolean || value instanceof Number ? value : String.valueOf(value);
    }
}
