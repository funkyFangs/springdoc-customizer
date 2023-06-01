package io.funky.fangs.springdoc.customizer.configuration;

import io.funky.fangs.springdoc.customizer.customizer.ExamplesOpenApiCustomizer;
import jakarta.validation.Validator;
import lombok.Getter;
import lombok.Setter;
import org.springdoc.core.properties.SpringDocConfigProperties;

/**
 * Configuration properties related the {@link ExamplesOpenApiCustomizer}.
 *
 * @author Harper Price
 * @since 2.1.0
 * @see SpringDocCustomizerConfigurationProperties
 * @see ExamplesOpenApiCustomizer
 */
@Getter
@Setter
public class ExamplesCustomizerConfigurationProperties {
    public static final String PREFIX = SpringDocCustomizerConfigurationProperties.PREFIX + ".examples";

    /**
     * Determines if examples should be validated based on a {@link Validator}, if available. Invalid examples will
     * not be included in the specification.
     */
    private boolean validateExamples = true;

    /**
     * Determines if {@link SpringDocConfigProperties#getDefaultConsumesMediaType()} should be targeted for examples
     * where applicable.
     */
    private boolean includeDefaultConsumesMediaType = true;

    /**
     * Determines if {@link SpringDocConfigProperties#getDefaultProducesMediaType()} should be targeted for examples
     * where applicable.
     */
    private boolean includeDefaultProducesMediaType = true;

    private boolean enabled;
}