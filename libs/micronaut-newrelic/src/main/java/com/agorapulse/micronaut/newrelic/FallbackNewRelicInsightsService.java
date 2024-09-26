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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.annotation.Secondary;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.runtime.context.scope.refresh.RefreshEvent;
import jakarta.inject.Singleton;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Secondary
@Singleton
public class FallbackNewRelicInsightsService implements NewRelicInsightsService, ApplicationEventListener<RefreshEvent> {

    private static final int MAX_EVENTS = 25;
    private static final Logger LOGGER = LoggerFactory.getLogger(NewRelicInsightsService.class);

    private final EventPayloadExtractor extractor;
    private final ObjectMapper mapper;
    private final Deque<Object> events = new ArrayDeque<>();

    public FallbackNewRelicInsightsService(EventPayloadExtractor extractor,
                                           ObjectMapper mapper) {
        this.extractor = extractor;
        this.mapper = mapper;
    }

    @Override
    public <E> void unsafeCreateEvents(@NonNull @Valid Collection<E> events) throws Exception {
        createEvents(events);
    }

    @Override
    public <E> void createEvents(Collection<E> events) {
        try {
            List<Map<String, Object>> payloads = events.stream().map(extractor::extractPayload).toList();
            LOGGER.info("Following events not sent to NewRelic:\n{}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(payloads));

            this.events.addAll(events);

            while (this.events.size() > MAX_EVENTS) {
                this.events.poll();
            }

            LOGGER.info("You can access {} event(s) using FallbackNewRelicInsightsService#getEvents() method.", this.events.size());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public <E> Stream<E> getEvents(Class<E> eventType) {
        return events.stream().filter(eventType::isInstance).map(eventType::cast);
    }

    @Override
    public void onApplicationEvent(RefreshEvent event) {
        events.clear();
    }

}
