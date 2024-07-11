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

import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Default NewRelicInsightsService, sends events to the New Relic API in real time, with a blocking request.
 * This implementation is the one to use in Lambdas, since they aren't running the Java agent that would be able to
 * send the events asynchronously.
 */
@Primary
@Singleton
@Requires(beans = NewRelicInsightsClient.class)
@Replaces(FallbackNewRelicInsightsService.class)
public class DefaultNewRelicInsightsService implements NewRelicInsightsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultNewRelicInsightsService.class);
    private static final String DEFAULT_ERROR_MESSAGE = "Exception creating New Relic events ";

    private final CriticalNewRelicInsightsClient criticalClient;
    private final NewRelicInsightsClient client;
    private final EventPayloadExtractor extractor;
    private final NewRelicConfiguration configuration;

    public DefaultNewRelicInsightsService(
        CriticalNewRelicInsightsClient criticalClient,
        NewRelicInsightsClient client,
        EventPayloadExtractor extractor,
        NewRelicConfiguration configuration
    ) {
        this.criticalClient = criticalClient;
        this.client = client;
        this.extractor = extractor;
        this.configuration = configuration;
    }

    public <E> void createEvents(@Valid @NonNull Collection<E> events) {
        try {
            unsafeCreateEvents(events);
        } catch (ConstraintViolationException cve) {
            // keep the validation exceptions
            throw cve;
        } catch (Exception ex) {
            boolean hasCriticalEvents = events.stream()
                .map(extractor::extractPayload)
                .anyMatch(EventPayloadExtractor::isCritical);

            if (hasCriticalEvents) {
                log(ex);
            } else {
                // only log events that won't trigger retry with critical client
                if (!NewRelicRetryPredicate.INSTANCE.test(ex)) {
                    log(ex);
                }
            }
        }
    }

    @Override
    public <E> void unsafeCreateEvents(@NonNull @Valid Collection<E> events) {
        List<Map<String, Object>> criticalEvents = events.stream()
            .map(extractor::extractPayload)
            .filter(EventPayloadExtractor::isCritical)
            .toList();

        List<Map<String, Object>> nonCriticalEvents = events.stream()
            .map(extractor::extractPayload)
            .filter(EventPayloadExtractor::isNonCritical)
            .toList();

        criticalEvents.forEach(event -> EventPayloadExtractor.removeCritical(event));
        nonCriticalEvents.forEach(event -> EventPayloadExtractor.removeCritical(event));

        if (!criticalEvents.isEmpty()) {
            this.criticalClient.createEvents(criticalEvents);
        }

        if (!nonCriticalEvents.isEmpty()) {
            this.client.createEvents(nonCriticalEvents);
        }
    }

    private void log(Exception ex) {
        switch (configuration.getLogLevel()) {
            case TRACE:
                LOGGER.trace(DEFAULT_ERROR_MESSAGE, ex);
                break;
            case DEBUG:
                LOGGER.debug(DEFAULT_ERROR_MESSAGE, ex);
                break;
            case INFO:
                LOGGER.info(DEFAULT_ERROR_MESSAGE, ex);
                break;
            case ERROR:
                LOGGER.error(DEFAULT_ERROR_MESSAGE, ex);
                break;
            case WARN:
            default:
                LOGGER.warn(DEFAULT_ERROR_MESSAGE, ex);
        }
    }

}
