package io.funky.fangs.springdoc.customizer.configuration;

import io.funky.fangs.springdoc.customizer.transformer.SpringDocCustomizerSwaggerIndexTransformer;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.webmvc.ui.SwaggerIndexTransformer;
import org.springdoc.webmvc.ui.SwaggerWelcomeCommon;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

import static org.springdoc.core.utils.Constants.SWAGGER_UI_PREFIX;
import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.SERVLET;

@AutoConfiguration
@ConditionalOnWebApplication(type = SERVLET)
@ConditionalOnProperty(prefix = SpringDocCustomizerConfigurationProperties.PREFIX, name = "enabled",
        matchIfMissing = true)
public class SpringDocCustomizerWebMvcAutoConfiguration {
    @Bean
    @ConditionalOnProperty(prefix = SWAGGER_UI_PREFIX, name = "enabled", matchIfMissing = true)
    public SwaggerIndexTransformer springDocCustomizerIndexTransformer(
            SpringDocCustomizerSwaggerUiConfigurationProperties configurationProperties,
            SwaggerUiConfigProperties swaggerUiConfig,
            SwaggerUiOAuthProperties swaggerUiOAuthProperties,
            SwaggerUiConfigParameters swaggerUiConfigParameters,
            SwaggerWelcomeCommon swaggerWelcomeCommon,
            ObjectMapperProvider objectMapperProvider) {
        return new SpringDocCustomizerSwaggerIndexTransformer(configurationProperties.getTitle(),
                configurationProperties.getLargeIconPath(), configurationProperties.getSmallIconPath(),
                swaggerUiConfig, swaggerUiOAuthProperties, swaggerUiConfigParameters, swaggerWelcomeCommon,
                objectMapperProvider);
    }
}
