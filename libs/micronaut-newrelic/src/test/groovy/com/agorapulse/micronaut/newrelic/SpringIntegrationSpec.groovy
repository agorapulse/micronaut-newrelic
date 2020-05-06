package com.agorapulse.micronaut.newrelic

import com.agorapulse.micronaut.grails.MicronautGrailsConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@SuppressWarnings('Instanceof')
@ContextConfiguration(classes = [NewRelicInsightsGrailsConfiguration, MicronautGrailsConfiguration])
class SpringIntegrationSpec extends Specification {

    @Autowired ApplicationContext applicationContext

    void 'configuration service is injected'() {
        when:
            Object bean = applicationContext.getBean('newRelicInsightsService')
        then:
            bean instanceof FallbackNewRelicInsightsService
    }

}

