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
package com.agorapulse.micronaut.newrelic

import io.micronaut.context.ApplicationContext
import spock.lang.AutoCleanup
import spock.lang.Specification
import spock.lang.Unroll

import javax.validation.ConstraintViolationException

@SuppressWarnings('Instanceof')
class FallbackSpec extends Specification {

    public static final String TEST_EVENT = 'TestEvent'

    @AutoCleanup ApplicationContext context

    NewRelicInsightsService service

    void setup() {
        context = ApplicationContext.builder().start()

        service = context.getBean(NewRelicInsightsService)
    }

    void 'default new relic instance is enabled'() {
        expect:
            !context.containsBean(NewRelicInsightsClient)
            service instanceof FallbackNewRelicInsightsService
    }

    void 'send simple event'() {
        when:
            service.createEvent(NewRelicInsightsEvent.create(TEST_EVENT))
        then:
            noExceptionThrown()
    }

    void 'send simple events'() {
        when:
            service.createEvents([NewRelicInsightsEvent.create(TEST_EVENT)])
        then:
            noExceptionThrown()
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
