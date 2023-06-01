package io.funky.fangs.springdoc.customizer.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties related to the Swagger UI.
 *
 * @author Harper Price
 * @since 2.1.0
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "springdoc.swagger-ui")
public class SpringDocCustomizerSwaggerUiConfigurationProperties {
    /**
     * Sets the title of the Swagger UI webpage to this value.
     */
    private String title;
    private String largeIconPath;
    private String smallIconPath;
}