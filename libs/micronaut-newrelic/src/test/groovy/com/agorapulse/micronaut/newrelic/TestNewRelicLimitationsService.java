package com.agorapulse.micronaut.newrelic;

import com.agorapulse.micronaut.newrelic.limitation.FallbackNewRelicLimitationsService;
import com.agorapulse.micronaut.newrelic.limitation.NewRelicLimitationsService;
import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Replaces;
import jakarta.inject.Singleton;

@Primary
@Replaces(FallbackNewRelicLimitationsService.class)
@Singleton
public class TestNewRelicLimitationsService implements NewRelicLimitationsService {

    public static final int MAX_VALUE_LENGTH = 20;

    @Override
    public int getMaxValueLength() {
        return MAX_VALUE_LENGTH;
    }
}
