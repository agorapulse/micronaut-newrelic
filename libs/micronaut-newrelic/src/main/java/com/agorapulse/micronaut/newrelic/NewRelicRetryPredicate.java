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

import io.micronaut.core.annotation.Introspected;
import io.micronaut.http.client.exceptions.HttpClientException;
import io.micronaut.http.client.exceptions.ReadTimeoutException;
import io.micronaut.retry.annotation.RetryPredicate;

import java.util.List;
import java.util.function.Predicate;

@Introspected
public class NewRelicRetryPredicate implements RetryPredicate {

    public static final Predicate<Throwable> INSTANCE = new NewRelicRetryPredicate();

    private static final List<String> RETRYABLE_MESSAGES = List.of(
        "request failed",
        "connection refused",
        "connection reset",
        "connection timed out",
        "no route to host",
        "read timed out",
        "write timed out"
    );

    @Override
    public boolean test(Throwable throwable) {
        if (!(throwable instanceof HttpClientException)) {
            return false;
        }

        if (throwable instanceof ReadTimeoutException) {
            return true;
        }

        String message = throwable.getMessage() == null ? "" : throwable.getMessage().toLowerCase();

        return RETRYABLE_MESSAGES.stream().anyMatch(message::contains);
    }

}
