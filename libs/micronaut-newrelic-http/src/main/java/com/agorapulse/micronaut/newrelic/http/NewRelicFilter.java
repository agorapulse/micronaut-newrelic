/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2020-2021 Agorapulse.
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
package com.agorapulse.micronaut.newrelic.http;

import com.newrelic.api.agent.Token;
import io.micronaut.http.HttpAttributes;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;

import java.util.concurrent.atomic.AtomicReference;

@Filter("/**")
public class NewRelicFilter implements HttpServerFilter {

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {
        AtomicReference<Token> token = new AtomicReference<>();
        return Flowable.fromCallable(() -> {
            String templateOrUri = request.getAttribute(HttpAttributes.URI_TEMPLATE, String.class).orElseGet(() -> request.getUri().toString());
            token.set(NewRelicUtil.startTransaction(templateOrUri));
            return true;
        }).switchMap(any -> chain.proceed(request)).doOnNext(resp -> {
            token.get().expire();
        });
    }
}
