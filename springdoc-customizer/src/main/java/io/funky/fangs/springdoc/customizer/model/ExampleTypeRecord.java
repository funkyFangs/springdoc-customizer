package io.funky.fangs.springdoc.customizer.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.funky.fangs.springdoc.customizer.annotation.ExampleType.Type;
import org.springframework.http.HttpStatus;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableSet;
import static java.util.function.Predicate.not;

public record ExampleTypeRecord(Type type, @JsonInclude(NON_EMPTY) Collection<String> mediaTypes,
                                @JsonInclude(NON_EMPTY) Collection<HttpStatus> responses) {
    public ExampleTypeRecord(Type type, Collection<String> mediaTypes, Collection<HttpStatus> responses) {
        this.type = type;
        this.mediaTypes = Optional.ofNullable(mediaTypes)
                .map(Set::copyOf)
                .orElse(emptySet());
        this.responses = Optional.ofNullable(responses)
                .filter(not(Collection::isEmpty))
                .map(collection -> unmodifiableSet(EnumSet.copyOf(collection)))
                .orElse(unmodifiableSet(EnumSet.noneOf(HttpStatus.class)));
    }
}