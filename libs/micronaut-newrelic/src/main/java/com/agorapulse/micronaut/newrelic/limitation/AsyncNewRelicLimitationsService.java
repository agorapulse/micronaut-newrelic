package com.agorapulse.micronaut.newrelic.limitation;

import com.agorapulse.micronaut.newrelic.AsyncNewRelicInsightsService;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;

@Requires(beans = AsyncNewRelicInsightsService.class)
@Singleton
public class AsyncNewRelicLimitationsService implements NewRelicLimitationsService {

    // https://docs.newrelic.com/docs/data-apis/custom-data/custom-events/data-requirements-limits-custom-event-data/#general
    private static final int MAX_VALUE_LENGTH = 255;

    @Override
    public int getMaxValueLength() {
        return MAX_VALUE_LENGTH;
    }
}
