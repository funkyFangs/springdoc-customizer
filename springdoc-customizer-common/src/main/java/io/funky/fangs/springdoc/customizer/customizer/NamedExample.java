package io.funky.fangs.springdoc.customizer.customizer;

import io.swagger.v3.oas.models.examples.Example;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * An extension of {@link Example} which supports a name.
 *
 * @author Harper Price
 * @since 2.1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NamedExample extends Example {
    private String name;

    public NamedExample name(String name) {
        setName(name);
        return this;
    }

    @Override
    public NamedExample summary(String summary) {
        setSummary(summary);
        return this;
    }

    @Override
    public NamedExample description(String description) {
        setDescription(description);
        return this;
    }

    @Override
    public NamedExample value(Object value) {
        setValue(value);
        return this;
    }
}