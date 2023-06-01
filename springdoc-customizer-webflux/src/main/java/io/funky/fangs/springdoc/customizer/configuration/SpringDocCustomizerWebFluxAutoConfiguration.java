package io.funky.fangs.springdoc.customizer.configuration;

import io.funky.fangs.springdoc.customizer.transformer.SpringDocCustomizerSwaggerIndexTransformer;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.webflux.ui.SwaggerIndexTransformer;
import org.springdoc.webflux.ui.SwaggerWelcomeCommon;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

import static org.springdoc.core.utils.Constants.SWAGGER_UI_PREFIX;
import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.REACTIVE;

@ConditionalOnWebApplication(type = REACTIVE)
@EnableAutoConfiguration
@ImportAutoConfiguration(SpringDocCustomizerAutoConfiguration.class)
public class SpringDocCustomizerWebFluxAutoConfiguration {
    @Bean
    @ConditionalOnWebApplication(type = REACTIVE)
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