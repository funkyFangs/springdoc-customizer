package io.funky.fangs.springdoc.customizer.annotation;

import io.swagger.v3.oas.models.OpenAPI;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation for examples which should be injected into the {@link OpenAPI} specification.
 *
 * @author Harper Price
 * @since 2.1.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface ExampleDetails {
    /**
     * The name of an example.
     */
    String name();

    /**
     * A brief summary of the example. This will be displayed in the drop-down menu of examples
     */
    String summary();

    /**
     * A full description of the example
     */
    String description();

    /**
     * Details determining where this example should be added to
     */
    ExampleTarget[] targets();
}