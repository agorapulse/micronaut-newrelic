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

import io.micronaut.context.annotation.ConfigurationProperties;

import io.micronaut.core.annotation.Nullable;
import org.slf4j.event.Level;

@ConfigurationProperties("newrelic")
public class NewRelicConfiguration {

    @Nullable private String url;
    @Nullable private String token;

    private Level logLevel = Level.WARN;

    @Nullable public String getUrl() {
        return url;
    }

    public void setUrl(@Nullable String url) {
        this.url = url;
    }

    @Nullable public String getToken() {
        return token;
    }

    public void setToken(@Nullable String token) {
        this.token = token;
    }

    public Level getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(Level logLevel) {
        this.logLevel = logLevel;
    }
}
