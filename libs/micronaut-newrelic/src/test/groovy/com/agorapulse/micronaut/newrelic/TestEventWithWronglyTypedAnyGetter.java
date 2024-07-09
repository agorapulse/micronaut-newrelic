package com.agorapulse.micronaut.newrelic;

import io.micronaut.core.annotation.Introspected;

import java.util.Map;

@Introspected
public class TestEventWithWronglyTypedAnyGetter implements NewRelicInsightsEvent {


    public TestEventWithWronglyTypedAnyGetter() {}

    @AnyGetter
    public Map getAdditionalData() {
        return Map.of(1, "OOPS");
    }
}
