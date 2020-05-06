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
package nr

import com.agorapulse.micronaut.newrelic.AsyncNewRelicInsightsService
import com.agorapulse.micronaut.newrelic.NewRelicInsightsClient
import com.agorapulse.micronaut.newrelic.NewRelicInsightsEvent
import com.agorapulse.micronaut.newrelic.NewRelicInsightsService
import com.newrelic.api.agent.Agent
import com.newrelic.api.agent.Insights
import io.micronaut.context.ApplicationContext
import spock.lang.AutoCleanup
import spock.lang.Specification
import spock.lang.Unroll

import javax.validation.ConstraintViolationException

@SuppressWarnings([
    'Instanceof',
    'DuplicateListLiteral',
])
class NewRelicLibSpec extends Specification {

    public static final String TEST_EVENT = 'TestEvent'

    @AutoCleanup ApplicationContext context

    NewRelicInsightsService service

    Agent agent = Mock()
    Insights insights = Mock()

    void setup() {
        context = ApplicationContext.build().build()

        context.registerSingleton(Agent, agent)
        context.registerSingleton(Insights, insights)

        context.start()

        service = context.getBean(NewRelicInsightsService)
    }

    void 'default new relic instance is enabled'() {
        expect:
            !context.containsBean(NewRelicInsightsClient)
            service instanceof AsyncNewRelicInsightsService
    }

    void 'send simple event'() {
        given:
            Map<String, Object> arguments = [:]
            insights.recordCustomEvent(TEST_EVENT, _) >> { String eventType, Map<String, Object> payload ->
                arguments = payload
            }
        when:
            service.createEvent(NewRelicInsightsEvent.create(TEST_EVENT))
        then:
            arguments
            !arguments.eventType
            arguments.timestamp
            arguments.value == 1L
    }

    void 'send simple events'() {
        given:
            Map<String, Object> arguments = [:]
            insights.recordCustomEvent(TEST_EVENT, _) >> { String eventType, Map<String, Object> payload ->
                arguments = payload
            }
        when:
            service.createEvents([NewRelicInsightsEvent.create(TEST_EVENT)])
        then:
            arguments
            !arguments.eventType
            arguments.timestamp
            arguments.value == 1L
    }

    @Unroll
    void 'event type must not be "#value"'() {
        when:
            service.createEvent(NewRelicInsightsEvent.create(''))
        then:
            thrown(ConstraintViolationException)
        where:
            value << [null, '']
    }

    @Unroll
    void 'event type must not be "#value" when sending events'() {
        when:
            service.createEvents([NewRelicInsightsEvent.create('')])
        then:
            thrown(ConstraintViolationException)
        where:
            value << [null, '']
    }

}
