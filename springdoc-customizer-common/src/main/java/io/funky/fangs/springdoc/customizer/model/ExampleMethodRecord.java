package io.funky.fangs.springdoc.customizer.model;

import io.funky.fangs.springdoc.customizer.annotation.ExampleMethod;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

/**
 * A record used for storing {@link ExampleMethod} data at run-time.
 *
 * @author Harper Price
 * @see ExampleMethod
 * @since 2.1.0
 */
public record ExampleMethodRecord(String name, Collection<ExampleTypeRecord> types) {
    public ExampleMethodRecord(String name, Collection<ExampleTypeRecord> types) {
        this.name = name;
        this.types = Optional.ofNullable(types)
                .map(List::copyOf)
                .orElse(emptyList());
    }
}