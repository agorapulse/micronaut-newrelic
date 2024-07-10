package com.agorapulse.micronaut.newrelic.limitation;

import com.agorapulse.micronaut.newrelic.AsyncNewRelicInsightsService;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;

@Requires(beans = AsyncNewRelicInsightsService.class)
@Singleton
public class AsyncNewRelicLimitationsService implements NewRelicLimitationsService {

    private static final int MAX_VALUE_LENGTH = 4096;

    @Override
    public int getMaxValueLength() {
        return MAX_VALUE_LENGTH;
    }
}
