package io.funky.fangs.springdoc.customizer.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.funky.fangs.springdoc.customizer.annotation.ExampleTarget;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static java.util.Collections.emptyList;

/**
 * @param controller
 * @param methods
 * @see ExampleTarget
 */
public record ExampleTargetRecord(String controller, @JsonInclude(NON_EMPTY) Collection<ExampleMethodRecord> methods) {
    public ExampleTargetRecord(String controller, Collection<ExampleMethodRecord> methods) {
        this.controller = controller;
        this.methods = Optional.ofNullable(methods)
                .map(List::copyOf)
                .orElse(emptyList());
    }
}