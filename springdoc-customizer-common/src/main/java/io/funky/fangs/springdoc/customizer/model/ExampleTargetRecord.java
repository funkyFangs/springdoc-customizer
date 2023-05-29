package io.funky.fangs.springdoc.customizer.model;

import io.funky.fangs.springdoc.customizer.annotation.ExampleTarget;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

/**
 * A record used for storing {@link ExampleTarget} data at run-time.
 *
 * @author Harper Price
 * @see ExampleTarget
 * @since 2.1.0
 */
public record ExampleTargetRecord(String controller, Collection<ExampleMethodRecord> methods) {
    public ExampleTargetRecord(String controller, Collection<ExampleMethodRecord> methods) {
        this.controller = controller;
        this.methods = Optional.ofNullable(methods)
                .map(List::copyOf)
                .orElse(emptyList());
    }
}