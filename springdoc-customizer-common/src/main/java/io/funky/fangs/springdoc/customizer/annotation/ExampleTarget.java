package io.funky.fangs.springdoc.customizer.annotation;

import org.springframework.stereotype.Controller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation denoting a {@link Controller} to target.
 *
 * @author Harper Price
 * @since 2.1.0
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface ExampleTarget {
    /**
     * The {@link Controller} to target.
     */
    Class<?> controller();

    /**
     * The details of the methods belonging to the {@link Controller} to target. The names of these methods must
     * exist as methods on the {@link Class} of the {@link Controller}.
     */
    ExampleMethod[] methods();
}