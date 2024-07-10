package com.agorapulse.micronaut.newrelic.limitation;

import com.agorapulse.micronaut.newrelic.DefaultNewRelicInsightsService;
import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;

@Primary
@Requires(beans = DefaultNewRelicInsightsService.class)
@Singleton
public class DefaultNewRelicLimitationsService implements NewRelicLimitationsService {

    private static final int MAX_VALUE_LENGTH = 4096;

    @Override
    public int getMaxValueLength() {
        return MAX_VALUE_LENGTH;
    }
}
