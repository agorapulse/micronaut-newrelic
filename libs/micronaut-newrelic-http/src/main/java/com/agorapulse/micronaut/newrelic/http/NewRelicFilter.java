package com.agorapulse.micronaut.newrelic.http;

import com.newrelic.api.agent.Agent;
import com.newrelic.api.agent.TransactionNamePriority;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpAttributes;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;

@Filter("/**")
@Requires(beans = Agent.class)
public class NewRelicFilter implements HttpServerFilter {

    private final Agent agent;

    public NewRelicFilter(Agent agent) {
        this.agent = agent;
    }

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {
        return Flowable.fromCallable(() -> {
            String templateOrUri = request.getAttribute(HttpAttributes.URI_TEMPLATE, String.class).orElseGet(() -> request.getUri().toString());
            agent.getTransaction().setTransactionName(TransactionNamePriority.REQUEST_URI, true, templateOrUri);
            return true;
        }).switchMap(any -> chain.proceed(request));
    }

}
