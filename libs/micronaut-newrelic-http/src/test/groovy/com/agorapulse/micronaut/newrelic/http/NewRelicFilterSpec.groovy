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
package com.agorapulse.micronaut.newrelic.http

import com.agorapulse.gru.Gru
import com.agorapulse.gru.http.Http
import io.micronaut.context.ApplicationContext
import io.micronaut.runtime.server.EmbeddedServer
import org.junit.Rule
import spock.lang.AutoCleanup
import spock.lang.Specification

class NewRelicFilterSpec extends Specification {

    @AutoCleanup ApplicationContext context
    @AutoCleanup EmbeddedServer server

    @Rule Gru gru = Gru.equip(Http.steal(this))

    void setup() {
        context = ApplicationContext.build().build()
        context.start()

        server = context.getBean(EmbeddedServer)
        server.start()

        gru.prepare(server.URL.toExternalForm())
    }

    void 'url is recorded'() {
        expect:
            gru.test {
                get '/test/foo/bar'
                expect {
                    text inline('foo/bar')
                }
            }
    }

}
