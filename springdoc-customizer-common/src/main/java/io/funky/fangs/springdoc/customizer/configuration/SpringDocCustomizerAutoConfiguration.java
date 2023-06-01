package io.funky.fangs.springdoc.customizer.configuration;

import io.funky.fangs.springdoc.customizer.customizer.ExamplesOpenApiCustomizer;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import jakarta.validation.Validator;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.lang.Nullable;

import java.util.Optional;

/**
 * An {@link AutoConfiguration} for customizing examples for an {@link OpenAPI} specification.
 *
 * @author Harper Price
 * @since 2.1.0
 */
@AutoConfiguration
@EnableConfigurationProperties({
        SpringDocCustomizerConfigurationProperties.class,
        SpringDocCustomizerSwaggerUiConfigurationProperties.class
})
@ConditionalOnProperty(prefix = SpringDocCustomizerConfigurationProperties.PREFIX, name = "enabled",
        matchIfMissing = true)
public class SpringDocCustomizerAutoConfiguration {
    /**
     * An {@link OpenApiCustomizer} bean that serializes and adds examples to an {@link OpenAPI} specification.
     */
    @Bean
    @ConditionalOnProperty(prefix = ExamplesCustomizerConfigurationProperties.PREFIX, name = "enabled",
            matchIfMissing = true)
    public OpenApiCustomizer examplesCustomizer(SpringDocCustomizerConfigurationProperties configurationProperties,
                                                @Nullable GroupedOpenApi groupedOpenApi,
                                                SpringDocConfigProperties springDocConfigProperties,
                                                @Nullable Validator validator) {
        var examplesConfigurationProperties = configurationProperties.getExamples();

        return new ExamplesOpenApiCustomizer(
                Optional.ofNullable(groupedOpenApi).map(GroupedOpenApi::getGroup).orElse(null),
                examplesConfigurationProperties.isIncludeDefaultConsumesMediaType()
                        ? springDocConfigProperties.getDefaultConsumesMediaType()
                        : null,
                examplesConfigurationProperties.isIncludeDefaultProducesMediaType()
                        ? springDocConfigProperties.getDefaultProducesMediaType()
                        : null,
                examplesConfigurationProperties.isValidateExamples() ? validator : null);
    }

    /**
     * An {@link OpenApiCustomizer} bean that sets the {@link OpenAPI}'s specification version to
     * the project's version.
     *
     * @param buildProperties a bean containing the project version
     * @return an {@link OpenApiCustomizer} that sets the {@link OpenAPI} specification version
     * @implNote This bean borrows a {@link Condition} from {@link ProjectInfoAutoConfiguration#buildProperties()},
     * because <code>@{@link ConditionalOnBean}({@link BuildProperties}.class)</code> always fails.
     */
    @Bean
    @ConditionalOnResource(resources = "${spring.info.build.location:classpath:META-INF/build-info.properties}")
    @ConditionalOnProperty(prefix = SpringDocCustomizerConfigurationProperties.PREFIX, name = "customize-version",
            matchIfMissing = true)
    public OpenApiCustomizer versionCustomizer(BuildProperties buildProperties) {
        // Populates info if null and sets version to build version
        return openApi -> Optional.ofNullable(openApi.getInfo())
                .orElseGet(() -> openApi.info(new Info()).getInfo())
                .setVersion(buildProperties.getVersion());
    }
}