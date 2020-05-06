/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2020 Agorapulse.
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Singleton;
import javax.validation.Valid;
import java.util.Collection;

/**
 * Default NewRelicInsightsService, sends events to the New Relic API in real time, with a blocking request.
 * This implementation is the one to use in Lambdas, since they aren't running the Java agent that would be able to
 * send the events asynchronously.
 */
@Singleton
@Requires(
    missingClasses = "com.newrelic.api.agent.NewRelic",
    beans = NewRelicInsightsClient.class
)
public class DefaultNewRelicInsightsService implements NewRelicInsightsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewRelicInsightsService.class);

    private final NewRelicInsightsClient client;

    public DefaultNewRelicInsightsService(NewRelicInsightsClient client) {
        this.client = client;
    }

    @Override
    public void createEvents(@Nonnull @Valid Collection<NewRelicInsightsEvent> events) {
        try {
            this.client.createEvents(events);
        } catch (Exception ex) {
            LOGGER.error("Exception creating New Relic events " + ex);
        }
    }
}
