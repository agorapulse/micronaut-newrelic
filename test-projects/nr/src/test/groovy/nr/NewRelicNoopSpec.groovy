/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2022-2022 Agorapulse.
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

import com.agorapulse.micronaut.newrelic.FallbackNewRelicInsightsService
import com.agorapulse.micronaut.newrelic.NewRelicInsightsClient
import com.agorapulse.micronaut.newrelic.NewRelicInsightsService
import io.micronaut.context.ApplicationContext
import spock.lang.Specification

@SuppressWarnings([
    'Instanceof',
    'DuplicateListLiteral',
])
class NewRelicNoopSpec extends Specification {

    void 'default new relic instance is disabled if there is no agent'() {
        given:
            ApplicationContext context = ApplicationContext.run()
            NewRelicInsightsService service = context.getBean(NewRelicInsightsService)
        expect:
            !context.containsBean(NewRelicInsightsClient)
            service instanceof FallbackNewRelicInsightsService
        cleanup:
            context.close()
    }

}
