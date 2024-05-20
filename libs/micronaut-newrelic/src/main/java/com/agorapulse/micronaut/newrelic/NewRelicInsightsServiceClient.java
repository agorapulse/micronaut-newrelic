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

import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.retry.annotation.Retryable;

import java.util.Map;

@Retryable
@NewRelicInsights
@Client("newrelic-insights")
@Requires(property = "micronaut.http.services.newrelic-insights.url", classes = HttpClient.class, beans = NewRelicInsightsClientFilter.class)
@Replaces(NewRelicInsightsUrlClient.class)
public interface NewRelicInsightsServiceClient extends NewRelicInsightsClient{

    @Override
    @Post("/events")
    @Header(name="Content-type", value="application/json")
    void createEvents(@Body Iterable<Map<String, Object>> events);

}
