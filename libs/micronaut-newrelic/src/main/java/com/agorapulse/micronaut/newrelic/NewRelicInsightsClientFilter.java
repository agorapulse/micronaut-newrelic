/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2022-2022 Agorapulse.
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
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.filter.ClientFilterChain;
import io.micronaut.http.filter.HttpClientFilter;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import javax.annotation.Nullable;
import javax.inject.Singleton;

@Singleton
@NewRelicInsights
@Requires(property = "newrelic.token")
public class NewRelicInsightsClientFilter implements HttpClientFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewRelicInsightsClientFilter.class);

    private final NewRelicConfiguration configuration;

    public NewRelicInsightsClientFilter(@Nullable NewRelicConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Publisher<? extends HttpResponse<?>> doFilter(MutableHttpRequest<?> request, ClientFilterChain chain) {
        return Flux.just(request)
            .map(r -> {
                if (configuration != null && StringUtils.isNotEmpty(configuration.getToken())) {
                    r.header("X-Insert-Key", configuration.getToken());
                }
                return r;
            })
            .switchMap(chain::proceed);

    }

}
