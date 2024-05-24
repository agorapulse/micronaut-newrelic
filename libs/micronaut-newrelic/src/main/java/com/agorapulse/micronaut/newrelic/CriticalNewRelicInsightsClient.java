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

import io.micronaut.context.annotation.Requires;
import io.micronaut.retry.annotation.Retryable;
import jakarta.inject.Singleton;

import java.util.Map;

/**
 * This client is used to send critical events to New Relic Insights.
 *
 * This class does not implement the {@link NewRelicInsightsClient} interface to avoid cyclic dependencies.
 */
@Singleton
@Requires(bean = NewRelicInsightsClient.class)
public class CriticalNewRelicInsightsClient {

    private final NewRelicInsightsClient client;

    public CriticalNewRelicInsightsClient(NewRelicInsightsClient client) {
        this.client = client;
    }

    @Retryable(attempts = "${newrelic.retry.count:3}", predicate = NewRelicRetryPredicate.class)
    public void createEvents(Iterable<Map<String, Object>> events) {
        client.createEvents(events);
    }

}
