package com.agorapulse.micronaut.newrelic.http

import com.agorapulse.gru.Gru
import com.agorapulse.gru.http.Http
import com.newrelic.api.agent.Agent
import com.newrelic.api.agent.Transaction
import com.newrelic.api.agent.TransactionNamePriority
import io.micronaut.context.ApplicationContext
import io.micronaut.runtime.server.EmbeddedServer
import org.junit.Rule
import spock.lang.AutoCleanup
import spock.lang.Specification

class NewRelicFilterSpec extends Specification {

    @AutoCleanup ApplicationContext context
    @AutoCleanup EmbeddedServer server

    @Rule Gru gru = Gru.equip(Http.steal(this))

    Transaction t = Mock()
    Agent agent = Mock {
        getTransaction() >> t
    }

    void setup() {
        context = ApplicationContext.build().build()
        context.registerSingleton(Agent, agent)
        context.start()

        server = context.getBean(EmbeddedServer)
        server.start()

        gru.prepare(server.URL.toExternalForm())
    }

    void 'url is recorded'() {
        when:
            gru.test {
                get '/test/foo/bar'
                expect {
                    text inline('foo/bar')
                }
            }
        then:
            gru.verify()

            1 * t.setTransactionName(TransactionNamePriority.REQUEST_URI, true, '/test/{one}/{two}')
    }

}
