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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * it allows having event attributes built dynamically from an annotated field/getter providing a Map<String, Object>
 * all keys/values will be sent as independent attributes to NewRelic
 */
@Inherited
@Documented
@Introspected
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Flatten {
}