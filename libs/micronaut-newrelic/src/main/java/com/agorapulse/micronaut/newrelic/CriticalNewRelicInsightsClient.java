package com.agorapulse.micronaut.newrelic;

import io.micronaut.context.annotation.Requires;
import io.micronaut.retry.annotation.Retryable;
import jakarta.inject.Singleton;

import java.util.Map;

/**
 * This client is used to send critical events to New Relic Insights.
 *
 * This class does not implement the {@link NewRelicInsightsClient} interface to avoid cyclic dependencies.
 */
@Singleton
@Requires(bean = NewRelicInsightsClient.class)
public class CriticalNewRelicInsightsClient {

    private final NewRelicInsightsClient client;

    public CriticalNewRelicInsightsClient(NewRelicInsightsClient client) {
        this.client = client;
    }

    @Retryable(attempts = "${newrelic.retry.count:3}", predicate = NewRelicRetryPredicate.class)
    public void createEvents(Iterable<Map<String, Object>> events) {
        client.createEvents(events);
    }

}
