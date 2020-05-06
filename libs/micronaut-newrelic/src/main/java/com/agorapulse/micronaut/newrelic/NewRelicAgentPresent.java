/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2020 Vladimir Orany.
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

public class NewRelicAgentPresent implements Condition {

    @Override
    public boolean matches(ConditionContext context) {
        try {
            Class<?> newRelicAgent = Class.forName("com.newrelic.api.agent.Agent");
            context.getBeanContext().getBean(newRelicAgent);
            boolean isNoop = newRelicAgent.getSimpleName().toLowerCase().contains("noop");
            if (isNoop) {
                context.fail("NewRelic agent is NoOpAgent");
                return false;
            }
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}
