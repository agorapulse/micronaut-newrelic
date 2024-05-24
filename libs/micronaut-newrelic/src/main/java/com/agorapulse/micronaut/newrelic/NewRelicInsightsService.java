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

import io.micronaut.validation.Validated;
import org.slf4j.LoggerFactory;

import io.micronaut.core.annotation.NonNull;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import java.util.Collection;
import java.util.Collections;

@Validated
public interface NewRelicInsightsService {

    default <E> void createEvent(@Valid @NonNull E event) {
        createEvents(Collections.singleton(event));
    }

    <E> void createEvents(@Valid @NonNull Collection<E> events);

    default <E> void unsafeCreateEvent(@Valid @NonNull E event) throws Exception {
        unsafeCreateEvents(Collections.singleton(event));
    }

    <E> void unsafeCreateEvents(@Valid @NonNull Collection<E> events) throws Exception;

}
