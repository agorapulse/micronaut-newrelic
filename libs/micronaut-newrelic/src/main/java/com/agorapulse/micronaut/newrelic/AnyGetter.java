package com.agorapulse.micronaut.newrelic;

import io.micronaut.core.annotation.Introspected;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Quite similar to Jackson @JsonAnyGetter
 * it allows having event attributes built dynamically from an annotated field/getter providing a Map<String, Object>
 * all keys/values will be sent as independent attributes to NewRelic
 */
@Inherited
@Documented
@Introspected
@Retention(RetentionPolicy.RUNTIME)
//@Target({ElementType.METHOD, ElementType.FIELD})
@Target({ElementType.METHOD})
public @interface AnyGetter {
}
