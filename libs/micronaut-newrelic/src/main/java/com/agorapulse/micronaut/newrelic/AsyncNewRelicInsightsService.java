/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2022-2023 Agorapulse.
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

import com.newrelic.api.agent.Insights;
import com.newrelic.api.agent.NewRelic;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;

import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import javax.validation.Valid;
import java.util.Collection;
import java.util.Map;

/**
 * Async NewRelicInsightsService, which uses the New Relic Java agent to send events by batch, every minute.
 */
@Singleton
@Requires(classes = NewRelic.class, condition = NewRelicAgentPresentCondition.class)
@Replaces(FallbackNewRelicInsightsService.class)
public class AsyncNewRelicInsightsService implements NewRelicInsightsService {

    private final Insights insights;
    private final EventPayloadExtractor extractor;

    public AsyncNewRelicInsightsService(Insights insights, EventPayloadExtractor extractor) {
        this.insights = insights;
        this.extractor = extractor;
    }

    @Override
    public <E> void unsafeCreateEvent(@NonNull @Valid E event) {
        Map<String, Object> map = extractor.extractPayload(event);
        Object eventType = map.remove("eventType");
        insights.recordCustomEvent(eventType.toString(), map);
    }

    @Override
    public <E> void unsafeCreateEvents(@NonNull @Valid Collection<E> events) {
        for (E event : events) {
            unsafeCreateEvent(event);
        }
    }

}
