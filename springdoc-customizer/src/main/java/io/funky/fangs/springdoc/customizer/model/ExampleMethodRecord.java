package io.funky.fangs.springdoc.customizer.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static java.util.Collections.emptyList;

public record ExampleMethodRecord(String name, @JsonInclude(NON_EMPTY) Collection<ExampleTypeRecord> types) {
    public ExampleMethodRecord(String name, Collection<ExampleTypeRecord> types) {
        this.name = name;
        this.types = Optional.ofNullable(types)
                .map(List::copyOf)
                .orElse(emptyList());
    }
}