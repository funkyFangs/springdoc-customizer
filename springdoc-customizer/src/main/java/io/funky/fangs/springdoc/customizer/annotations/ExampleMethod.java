package io.funky.fangs.springdoc.customizer.annotations;

import org.springframework.stereotype.Controller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/**
 * An annotation representing a {@link Method} to target on a {@link Controller} and its respective requests and/or
 * responses.
 *
 * @author Harper Price
 * @since 2.1.0
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExampleMethod {
    /**
     * The name of the method to target. This method must have an associated HTTP mapping
     */
    String name();

    /**
     * Details about the method's HTTP mapping to target
     */
    ExampleType[] types();
}