/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2021 Agorapulse.
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
import io.micronaut.retry.annotation.Fallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Singleton;
import javax.validation.Valid;
import java.util.Collection;

@Fallback
@Singleton
public class FallbackNewRelicInsightsService implements NewRelicInsightsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewRelicInsightsService.class);

    private final ObjectMapper mapper;

    public FallbackNewRelicInsightsService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public <E> void createEvents(@Nonnull @Valid Collection<E> events) {
        try {
            LOGGER.info("Following events not sent to NewRelic:\n" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(events));
        } catch (JsonProcessingException e) {
            LOGGER.info("Cannot log events: " + events);
        }
    }
}
