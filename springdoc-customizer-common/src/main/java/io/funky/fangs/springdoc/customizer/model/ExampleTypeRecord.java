package io.funky.fangs.springdoc.customizer.model;

import io.funky.fangs.springdoc.customizer.annotation.ExampleType;
import io.funky.fangs.springdoc.customizer.annotation.ExampleType.Type;
import org.springframework.http.HttpStatus;

import java.util.*;

import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableSet;
import static java.util.function.Predicate.not;

/**
 * A record used for storing {@link ExampleType} data at run-time.
 *
 * @author Harper Price
 * @see ExampleType
 * @since 2.1.0
 */
public record ExampleTypeRecord(Type type, Collection<String> mediaTypes, Collection<HttpStatus> responses) {
    public ExampleTypeRecord(Type type, Collection<String> mediaTypes, Collection<HttpStatus> responses) {
        this.type = type;
        this.mediaTypes = Optional.ofNullable(mediaTypes)
                .map(Set::copyOf)
                .orElse(emptySet());
        this.responses = Optional.ofNullable(responses)
                .filter(not(Collection::isEmpty))
                // Creates an unmodifiable view of an EnumSet for data integrity and performance
                .map(EnumSet::copyOf)
                .map(Collections::unmodifiableSet)
                .orElse(unmodifiableSet(EnumSet.noneOf(HttpStatus.class)));
    }
}