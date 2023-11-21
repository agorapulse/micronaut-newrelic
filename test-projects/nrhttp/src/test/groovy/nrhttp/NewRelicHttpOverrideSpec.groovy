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
package nrhttp

import com.agorapulse.micronaut.newrelic.DefaultNewRelicInsightsService
import com.agorapulse.micronaut.newrelic.NewRelicInsightsClient
import com.agorapulse.micronaut.newrelic.NewRelicInsightsService
import com.newrelic.api.agent.Agent
import io.micronaut.context.ApplicationContext
import spock.lang.AutoCleanup
import spock.lang.Specification

@SuppressWarnings([
    'Instanceof',
    'DuplicateListLiteral',
])
class NewRelicHttpOverrideSpec extends Specification {

    @AutoCleanup ApplicationContext context

    NewRelicInsightsService service

    void setup() {
        context = ApplicationContext.builder(
            'newrelic.token': 'nrtoken',
            'newrelic.url': 'http://example.com/nr'
        ).build()

        // this will break the noop check but still the HTTP implementation should win
        context.registerSingleton(Agent, Mock(Agent))

        context.start()
        service = context.getBean(NewRelicInsightsService)
    }

    void 'http implementation has a priority over agent based'() {
        expect:
            context.containsBean(NewRelicInsightsClient)
            service instanceof DefaultNewRelicInsightsService
    }

}
