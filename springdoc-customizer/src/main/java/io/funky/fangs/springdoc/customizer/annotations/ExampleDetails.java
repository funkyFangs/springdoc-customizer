package io.funky.fangs.springdoc.customizer.annotations;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.aot.hint.annotation.Reflective;

import java.lang.annotation.*;

/**
 * An annotation for examples which should be injected into the {@link OpenAPI} specification.
 *
 * @author Harper Price
 * @since 2.1.0
 */
// TODO: Investigate @Reflective annotation
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
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