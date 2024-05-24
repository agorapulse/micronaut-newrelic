package com.agorapulse.micronaut.newrelic;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.http.client.exceptions.HttpClientException;
import io.micronaut.http.client.exceptions.ReadTimeoutException;
import io.micronaut.retry.annotation.RetryPredicate;

import java.util.List;
import java.util.function.Predicate;

@Introspected
public class NewRelicRetryPredicate implements RetryPredicate {

    public static Predicate<Throwable> INSTANCE = new NewRelicRetryPredicate();

    private static final List<String> RETRYABLE_MESSAGES = List.of(
        "request failed",
        "connection refused",
        "connection reset",
        "connection timed out",
        "no route to host",
        "read timed out",
        "write timed out"
    );

    @Override
    public boolean test(Throwable throwable) {
        if (!(throwable instanceof HttpClientException)) {
            return false;
        }

        if (throwable instanceof ReadTimeoutException) {
            return true;
        }

        String message = throwable.getMessage() == null ? "" : throwable.getMessage().toLowerCase();

        return RETRYABLE_MESSAGES.stream().anyMatch(message::contains);
    }

}
