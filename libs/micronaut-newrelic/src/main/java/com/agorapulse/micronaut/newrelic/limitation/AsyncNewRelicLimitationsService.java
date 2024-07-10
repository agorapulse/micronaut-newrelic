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
package com.agorapulse.micronaut.newrelic.limitation;

import com.agorapulse.micronaut.newrelic.AsyncNewRelicInsightsService;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;

@Requires(beans = AsyncNewRelicInsightsService.class)
@Singleton
public class AsyncNewRelicLimitationsService implements NewRelicLimitationsService {

    // https://docs.newrelic.com/docs/data-apis/custom-data/custom-events/data-requirements-limits-custom-event-data/#general
    private static final int MAX_VALUE_LENGTH = 255;

    @Override
    public int getMaxValueLength() {
        return MAX_VALUE_LENGTH;
    }
}
