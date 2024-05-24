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
import jakarta.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Default NewRelicInsightsService, sends events to the New Relic API in real time, with a blocking request.
 * This implementation is the one to use in Lambdas, since they aren't running the Java agent that would be able to
 * send the events asynchronously.
 */
@Primary
@Singleton
@Requires(
    beans = NewRelicInsightsClient.class
)
@Replaces(FallbackNewRelicInsightsService.class)
public class DefaultNewRelicInsightsService implements NewRelicInsightsService {

    private final NewRelicInsightsClient client;
    private final EventPayloadExtractor extractor;

    public DefaultNewRelicInsightsService(NewRelicInsightsClient client, EventPayloadExtractor extractor) {
        this.client = client;
        this.extractor = extractor;
    }

    @Override
    public <E> void unsafeCreateEvents(@NonNull @Valid Collection<E> events) {
        this.client.createEvents(events.stream().map(extractor::extractPayload).collect(Collectors.toList()));
    }

}
