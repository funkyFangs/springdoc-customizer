package io.funky.fangs.springdoc.customizer.annotations;

import org.springframework.http.HttpStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation which determines the {@link Type} of example to target; this includes fields relevant to that type,
 * such as {@link HttpStatus}es for {@link Type#RESPONSE} examples.
 *
 * @author Harper Price
 * @since 2.1.0
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExampleType {
    /**
     * The type of the example.
     */
    Type value();

    /**
     * The media type values to target for an example.
     */
    String[] mediaTypes() default {};

    /**
     * The responses to target for an example. This should only be populated for {@link Type#RESPONSE} examples.
     */
    HttpStatus[] responses() default {};

    /**
     * An enumeration used to determine if an example should be injected into a request or response(s).
     */
    enum Type {
        REQUEST,
        RESPONSE
    }
}