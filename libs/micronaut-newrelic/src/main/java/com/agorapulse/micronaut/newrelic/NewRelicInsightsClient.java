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
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;

@Client("${newrelic.url}")
@Header(name="X-Insert-Key", value="${newrelic.token}")
@Requires(property = "newrelic.url", classes = RxHttpClient.class)
@Requires(property = "newrelic.token")
public interface NewRelicInsightsClient {

    @Post("/events")
    @Header(name="Content-type", value="application/json")
    void createEvents(@Body Iterable<NewRelicInsightsEvent> events);

}
