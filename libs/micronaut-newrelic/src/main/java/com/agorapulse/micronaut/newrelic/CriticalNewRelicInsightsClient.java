package com.agorapulse.micronaut.newrelic;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.retry.annotation.Retryable;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Singleton
@Named("critical")
public class CriticalNewRelicInsightsClient implements NewRelicInsightsClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(CriticalNewRelicInsightsClient.class);

    private final NewRelicInsightsServiceClient serviceClient;
    private final NewRelicInsightsUrlClient urlClient;

    public CriticalNewRelicInsightsClient(
        @Nullable NewRelicInsightsServiceClient serviceClient,
        @Nullable NewRelicInsightsUrlClient urlClient
    ) {
        this.serviceClient = serviceClient;
        this.urlClient = urlClient;
    }

    @Override
    @Retryable(attempts = "${newrelic.retry.counts:3}")
    public void createEvents(Iterable<Map<String, Object>> events) {
        if (serviceClient != null) {
            serviceClient.createEvents(events);
        } else if (urlClient != null) {
            urlClient.createEvents(events);
        } else {
            LOGGER.error("No New Relic client found to send events: {}", events);
        }
    }

}
