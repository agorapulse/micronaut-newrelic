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
package nonr

import com.agorapulse.micronaut.newrelic.NewRelicInsightsClient
import com.agorapulse.micronaut.newrelic.NewRelicInsightsEvent
import com.agorapulse.micronaut.newrelic.NewRelicInsightsService
import io.micronaut.context.annotation.Property
import io.micronaut.http.client.exceptions.HttpClientException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest
@Property(name = 'newrelic.retry.count', value = '1')
@Property(name = 'newrelic.log-level', value = 'error')
class CriticalEventsRetiresSpec extends Specification {

    @Inject NewRelicInsightsService service

    @MockBean(NewRelicInsightsClient)
    NewRelicInsightsClient newRelicInsightsClient = Mock(NewRelicInsightsClient)

    void 'send critical event without issues'() {
        when:
            service.createEvent(new CriticalEvent('test'))
        then:
            1 * newRelicInsightsClient.createEvents(_ as List)
    }

    void 'send critical event with one retryable issue #error'(Throwable error) {
        when:
            service.createEvent(new CriticalEvent('test'))
        then:
            noExceptionThrown()

            1 * newRelicInsightsClient.createEvents(_ as List) >> { throw error }
            1 * newRelicInsightsClient.createEvents(_ as List)
        where:
            error << [
                new HttpClientException('request failed'),
                new HttpClientException('connection refused'),
                new HttpClientException('connection reset'),
                new HttpClientException('connection timed out'),
                new HttpClientException('no route to host'),
                new HttpClientException('read timed out'),
                new HttpClientException('write timed out'),
            ]
    }

    void 'send critical event with two retryable issues'() {
        when:
            service.createEvent(new CriticalEvent('test'))
        then:
            noExceptionThrown()

            2 * newRelicInsightsClient.createEvents(_ as List) >> { throw new HttpClientException('request failed') }
            0 * newRelicInsightsClient.createEvents(_ as List)
    }

    void 'send critical event with one non-retryable issue #error'(Throwable error) {
        when:
            service.createEvent(new CriticalEvent('test'))
        then:
            noExceptionThrown()

            1 * newRelicInsightsClient.createEvents(_ as List) >> { throw error }
            0 * newRelicInsightsClient.createEvents(_ as List)
        where:
            error << [
                new HttpClientException('something happened'),
                new IllegalArgumentException('other error') ,
            ]
    }

    void 'send normal event with one retryable issue'() {
        when:
            service.createEvent(NewRelicInsightsEvent.create('test'))
        then:
            noExceptionThrown()

            1 * newRelicInsightsClient.createEvents(_ as List) >> { throw new HttpClientException('request failed') }
            0 * newRelicInsightsClient.createEvents(_ as List)
    }

}
