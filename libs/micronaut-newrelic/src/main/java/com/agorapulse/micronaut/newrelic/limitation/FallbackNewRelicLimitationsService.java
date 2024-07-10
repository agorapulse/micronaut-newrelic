package com.agorapulse.micronaut.newrelic.limitation;

import com.agorapulse.micronaut.newrelic.DefaultNewRelicInsightsService;
import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;

@Primary
@Requires(beans = DefaultNewRelicInsightsService.class)
@Singleton
public class FallbackNewRelicLimitationsService implements NewRelicLimitationsService {

    @Override
    public int getMaxValueLength() {
        return Integer.MAX_VALUE;
    }
}
