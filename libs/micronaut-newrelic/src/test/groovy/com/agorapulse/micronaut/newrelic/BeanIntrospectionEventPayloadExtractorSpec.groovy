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
package com.agorapulse.micronaut.newrelic

import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest
class BeanIntrospectionEventPayloadExtractorSpec extends Specification {

    @Inject
    BeanIntrospectionEventPayloadExtractor extractor

    void 'extract payload from event with AnyGetter'() {
        given:
            String message = 'some message'
            String firstKey = 'firstKey'
            String firstValue = 'firstValue'
            String secondKey = 'secondKey'
            String secondValue = 42
            TestEvent event = new TestEvent(message, Map.of(firstKey, firstValue, secondKey, secondValue))
        when:
            Map<String, Object> payload = extractor.extractPayload(event)
        then:
            println 'Payload: ' + payload
            payload.eventType == 'TestEvent'
            payload.timestamp
            payload.message == message
            payload.firstKey == firstValue
            payload.secondKey == secondValue
    }

    void 'extract payload from event with null AnyGetter value'() {
        given:
            String message = 'some message'
            TestEvent event = new TestEvent(message, null)
        when:
            Map<String, Object> payload = extractor.extractPayload(event)
        then:
            println 'Payload: ' + payload
            noExceptionThrown()
            payload.eventType == 'TestEvent'
            payload.timestamp
            payload.message == message
            !payload.firstKey
            !payload.secondKey
    }

    void 'extract payload from event with empty AnyGetter value'() {
        given:
            String message = 'some message'
            TestEvent event = new TestEvent(message, [:])
        when:
            Map<String, Object> payload = extractor.extractPayload(event)
        then:
            println 'Payload: ' + payload
            noExceptionThrown()
            payload.eventType == 'TestEvent'
            payload.timestamp
            payload.message == message
            !payload.firstKey
            !payload.secondKey
    }

    void 'extract payload should fail if AnyGetter map has non string key'() {
        given:
            TestEventWithWronglyTypedAnyGetter event = new TestEventWithWronglyTypedAnyGetter()
        when:
            extractor.extractPayload(event)
        then:
            IllegalArgumentException e = thrown(IllegalArgumentException)
            e.message == 'AnyGetter annotated getter must return Map<String, Object> but found a non String key in {1=OOPS}'
    }
}
