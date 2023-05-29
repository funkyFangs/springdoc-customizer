package io.funky.fangs.springdoc.customizer.model;

import io.funky.fangs.springdoc.customizer.annotation.ExampleDetails;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

/**
 * A record used for storing {@link ExampleDetails} data at run-time.
 *
 * @author Harper Price
 * @see ExampleDetails
 * @since 2.1.0
 */
public record ExampleDetailsRecord(String name, String summary, String description,
                                   Collection<ExampleTargetRecord> targets) {
    public ExampleDetailsRecord(String name, String summary, String description,
                                Collection<ExampleTargetRecord> targets) {
        this.name = name;
        this.summary = summary;
        this.description = description;
        this.targets = Optional.ofNullable(targets)
                .map(List::copyOf)
                .orElse(emptyList());
    }
}