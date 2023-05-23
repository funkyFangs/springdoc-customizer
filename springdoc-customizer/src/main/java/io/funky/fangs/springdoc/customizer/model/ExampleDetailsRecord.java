package io.funky.fangs.springdoc.customizer.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.funky.fangs.springdoc.customizer.annotation.ExampleDetails;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static java.util.Collections.emptyList;

/**
 *
 * @param name
 * @param summary
 * @param description
 * @param targets
 * @see ExampleDetails
 */
public record ExampleDetailsRecord(String name, String summary, String description,
                                   @JsonInclude(NON_EMPTY) Collection<ExampleTargetRecord> targets) {
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