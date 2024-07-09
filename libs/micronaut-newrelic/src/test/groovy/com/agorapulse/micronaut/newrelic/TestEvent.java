package com.agorapulse.micronaut.newrelic;

import io.micronaut.core.annotation.Introspected;

import java.util.Map;

@Introspected
public class TestEvent implements NewRelicInsightsEvent {

    private final String message;
    private final Map<String, String> someNameForAdditionalData;

    public TestEvent(String message, Map<String, String> additionalData) {
        this.message = message;
        this.someNameForAdditionalData = additionalData;
    }

    @AnyGetter
    public Map<String, String> getAdditionalData() {
        return someNameForAdditionalData;
    }

    public String getMessage() {
        return message;
    }
}
