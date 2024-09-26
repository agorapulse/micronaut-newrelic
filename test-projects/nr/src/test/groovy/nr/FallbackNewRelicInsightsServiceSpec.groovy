package nr

import com.agorapulse.micronaut.newrelic.FallbackNewRelicInsightsService
import com.agorapulse.micronaut.newrelic.NewRelicInsightsEvent
import groovy.transform.CompileDynamic
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@CompileDynamic
@MicronautTest
class FallbackNewRelicInsightsServiceSpec extends Specification {

    @Inject FallbackNewRelicInsightsService service

    void 'events are reset 1'() {
        expect:
            service.getEvents(NewRelicInsightsEvent).count() == 0
        when:
            service.createEvent(NewRelicInsightsEvent.create('TestEvent', 'order', 1))
        then:
            service.getEvents(NewRelicInsightsEvent).count() == 1
    }

    void 'events are reset 2'() {
        expect:
            service.getEvents(NewRelicInsightsEvent).count() == 0
        when:
            service.createEvent(NewRelicInsightsEvent.create('TestEvent', 'order', 1))
        then:
            service.getEvents(NewRelicInsightsEvent).count() == 1
    }

}
