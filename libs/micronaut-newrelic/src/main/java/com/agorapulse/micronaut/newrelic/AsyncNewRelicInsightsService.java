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

import com.newrelic.api.agent.Insights;
import com.newrelic.api.agent.NewRelic;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.core.beans.BeanIntrospector;

import javax.annotation.Nonnull;
import javax.inject.Singleton;
import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Async NewRelicInsightsService, which uses the New Relic Java agent to send events by batch, every minute.
 */
@Singleton
@Requires(classes = NewRelic.class, condition = NewRelicAgentPresentCondition.class)
public class AsyncNewRelicInsightsService implements NewRelicInsightsService {

    private final Insights insights;
    private final BeanIntrospector introspector = BeanIntrospector.SHARED;

    public AsyncNewRelicInsightsService(Insights insights) {
        this.insights = insights;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void createEvent(@Nonnull @Valid NewRelicInsightsEvent event) {
        BeanIntrospection<NewRelicInsightsEvent> introspection = (BeanIntrospection<NewRelicInsightsEvent>) introspector
            .findIntrospection(event.getClass())
            .orElseThrow(() ->
                //should never happen ad the core event is annotated with @Introspected
                new IllegalArgumentException("Cannot find introspection for class " + event.getClass() + ". Please, annotate the class with @Introspected")
            );

        String[] propertyNames = introspection.getPropertyNames();
        Map<String, Object> map = new HashMap<>(propertyNames.length - 1);

        for (String name : propertyNames) {
            if ("eventType".equals(name)) {
                continue;
            }
            map.put(name, introspection.getProperty(name).map(p -> p.get(event)).orElse(null));
        }

        insights.recordCustomEvent(event.getEventType(), map);
    }

    @Override
    public void createEvents(@Nonnull @Valid Collection<NewRelicInsightsEvent> events) {
        for (NewRelicInsightsEvent event : events) {
            createEvent(event);
        }
    }

}
