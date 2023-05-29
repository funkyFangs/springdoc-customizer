package io.funky.fangs.springdoc.customizer.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import lombok.Getter;
import lombok.Setter;
import org.springdoc.core.utils.Constants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import static io.funky.fangs.springdoc.customizer.configuration.SpringDocCustomizerConfigurationProperties.PREFIX;

/**
 * {@link ConfigurationProperties} used for adding examples to an {@link OpenAPI} specification.
 *
 * @author Harper Price
 * @since 2.1.0
 */
@Getter
@Setter
@ConfigurationProperties(prefix = PREFIX)
public class SpringDocCustomizerConfigurationProperties {
    public static final String PREFIX = Constants.SPRINGDOC_PREFIX + ".customizer";

    @NestedConfigurationProperty
    private ExamplesCustomizerConfigurationProperties examples = new ExamplesCustomizerConfigurationProperties();

    private boolean enabled;
}