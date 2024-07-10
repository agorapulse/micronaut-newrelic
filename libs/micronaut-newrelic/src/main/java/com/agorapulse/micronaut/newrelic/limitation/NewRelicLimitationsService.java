package com.agorapulse.micronaut.newrelic.limitation;

public interface NewRelicLimitationsService {

    default Object truncateValue(Object value) {
        if (value instanceof String valueString) {
            return valueString.substring(0, getMaxValueLength());
        }
        return value;
    }

    int getMaxValueLength();
}
