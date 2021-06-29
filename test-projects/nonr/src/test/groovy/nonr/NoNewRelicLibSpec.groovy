/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2021 Agorapulse.
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
package nonr

import com.agorapulse.micronaut.newrelic.DefaultNewRelicInsightsService
import com.agorapulse.micronaut.newrelic.NewRelicInsightsClient
import com.agorapulse.micronaut.newrelic.NewRelicInsightsEvent
import com.agorapulse.micronaut.newrelic.NewRelicInsightsService
import com.stehno.ersatz.ContentType
import com.stehno.ersatz.ErsatzServer
import io.micronaut.context.ApplicationContext
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
import spock.lang.AutoCleanup
import spock.lang.Specification
import spock.lang.Unroll

import javax.validation.ConstraintViolationException

@SuppressWarnings('Instanceof')
class NoNewRelicLibSpec extends Specification {

    private static final String NEW_RELIC_TOKEN = 'NEW_RELIC_TOKEN'
    public static final String TEST_EVENT = 'TestEvent'

    @AutoCleanup ApplicationContext context
    @AutoCleanup ErsatzServer server

    NewRelicInsightsService service

    Matcher<Map<String, Object>> bodyMatcher = new TypeSafeMatcher<Map<String, Object>>() {

        @Override
        void describeTo(Description description) {
            description.appendText('body containing event key, timestamp and value')
        }

        @Override
        protected boolean matchesSafely(Map<String, Object> item) {
            return item.containsKey('eventKey') && item.containsKey('timestamp') && item.containsKey('value')
        }

    }

    void setup() {
        server = new ErsatzServer({
            expectations {
                post '/events', {
                    header 'X-Insert-Key', NEW_RELIC_TOKEN
                    body(Matchers.contains(bodyMatcher), ContentType.APPLICATION_JSON)
                    responds().code(200)
                }
            }

            reportToConsole()
        })
        server.start()

        context = ApplicationContext.build(
            'newrelic.url': server.httpUrl,
            'newrelic.token': NEW_RELIC_TOKEN
        ).start()

        service = context.getBean(NewRelicInsightsService)
    }

    void 'default new relic instance is enabled'() {
        expect:
            context.containsBean(NewRelicInsightsClient)
            service instanceof DefaultNewRelicInsightsService
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
