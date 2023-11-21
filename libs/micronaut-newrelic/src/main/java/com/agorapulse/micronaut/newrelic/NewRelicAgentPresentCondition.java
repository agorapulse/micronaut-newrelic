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

import io.micronaut.context.condition.Condition;
import io.micronaut.context.condition.ConditionContext;

import java.lang.reflect.InvocationTargetException;

public class NewRelicAgentPresentCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context) {
        try {
            Class<?> newRelic = Class.forName("com.newrelic.api.agent.NewRelic");
            Object agent = newRelic.getMethod("getAgent").invoke(null);
            boolean isNoop = agent.getClass().getSimpleName().toLowerCase().contains("noop");
            if (isNoop) {
                context.fail("NewRelic agent is NoOpAgent");
                return false;
            }
            return true;
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return false;
        }
    }

}
