package io.funky.fangs.springdoc.customizer.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springframework.boot.info.BuildProperties;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpringDocCustomizerAutoConfigurationTest {/*
    @Mock
    private BuildProperties buildProperties;

    @Mock(answer = RETURNS_DEEP_STUBS) // Used for ExamplesConfigurationProperties
    private SpringDocCustomizerConfigurationProperties configurationProperties;

    @Mock
    private GroupedOpenApi groupedOpenApi;

    @Mock
    private SpringDocConfigProperties springDocConfigProperties;

    @Mock
    private Validator validator;

    private final SpringDocCustomizerAutoConfiguration autoConfiguration = new SpringDocCustomizerAutoConfiguration();

    @Test
    @DisplayName("examplesCustomizer test")
    void examplesCustomizerTest() {
        var group = "test";
        var mediaType = "application/json";
        var packagesToScan = Set.of(getClass().getPackageName());

        stubConfigurationProperties(packagesToScan, true, true);
        stubSpringDocConfigProperties(mediaType);
        stubGroupedOpenApi(group);

        var customizer = autoConfiguration.examplesCustomizer(configurationProperties, groupedOpenApi,
                springDocConfigProperties, validator);

        var examplesCustomizer = isExamplesCustomizer(customizer);

        assertThat(examplesCustomizer.getGroup()).isSameAs(group);
        assertThat(examplesCustomizer.getDefaultConsumesMediaType()).isSameAs(mediaType);
        assertThat(examplesCustomizer.getDefaultProducesMediaType()).isSameAs(mediaType);
        assertThat(examplesCustomizer.getValidator()).isSameAs(validator);
        assertThat(examplesCustomizer.getReflections()).isNotNull();

        verify(configurationProperties.getExamples()).getPackagesToScan();
    }

    @Test
    @DisplayName("examplesCustomizer without GroupedOpenApi test")
    void examplesCustomizerWithoutGroupedOpenApiTest() {
        var mediaType = "application/json";
        var packagesToScan = Set.of(getClass().getPackageName());

        stubConfigurationProperties(packagesToScan, true, true);
        stubSpringDocConfigProperties(mediaType);

        var customizer = autoConfiguration.examplesCustomizer(configurationProperties, null,
                springDocConfigProperties, validator);

        var examplesCustomizer = isExamplesCustomizer(customizer);

        assertThat(examplesCustomizer.getGroup()).isEmpty();
    }

    @Test
    @DisplayName("examplesCustomizer without Validator test")
    void examplesCustomizerWithoutValidatorTest() {
        var mediaType = "application/json";
        var packagesToScan = Set.of(getClass().getPackageName());
        var group = "test";

        stubConfigurationProperties(packagesToScan, true, true);
        stubSpringDocConfigProperties(mediaType);
        stubGroupedOpenApi(group);

        var customizer = autoConfiguration.examplesCustomizer(configurationProperties, groupedOpenApi,
                springDocConfigProperties, null);

        var examplesCustomizer = isExamplesCustomizer(customizer);

        assertThat(examplesCustomizer.getValidator()).isNull();
    }

    @Test
    @DisplayName("examplesCustomizer with validation disabled test")
    void examplesCustomizerWithValidationDisabledTest() {
        var mediaType = "application/json";
        var packagesToScan = Set.of(getClass().getPackageName());
        var group = "test";

        stubConfigurationProperties(packagesToScan, false, true);
        stubSpringDocConfigProperties(mediaType);
        stubGroupedOpenApi(group);

        var customizer = autoConfiguration.examplesCustomizer(configurationProperties, groupedOpenApi,
                springDocConfigProperties, validator);

        var examplesCustomizer = isExamplesCustomizer(customizer);

        assertThat(examplesCustomizer.getValidator()).isNull();
    }

    @Test
    @DisplayName("examplesCustomizer without default media types test")
    void examplesCustomizerWithoutDefaultMediaTypesTest() {
        var packagesToScan = Set.of(getClass().getPackageName());
        var group = "test";

        stubConfigurationProperties(packagesToScan, true, true);
        stubGroupedOpenApi(group);

        var customizer = autoConfiguration.examplesCustomizer(configurationProperties, groupedOpenApi,
                springDocConfigProperties, validator);

        var examplesCustomizer = isExamplesCustomizer(customizer);

        assertThat(examplesCustomizer.getDefaultProducesMediaType()).isNull();
        assertThat(examplesCustomizer.getDefaultConsumesMediaType()).isNull();
    }

    @Test
    @DisplayName("examplesCustomizer with default media types disabled test")
    void examplesCustomizerWithDefaultMediaTypesDisabledTest() {
        var packagesToScan = Set.of(getClass().getPackageName());
        var group = "test";

        stubConfigurationProperties(packagesToScan, true, false);
        stubGroupedOpenApi(group);

        var customizer = autoConfiguration.examplesCustomizer(configurationProperties, groupedOpenApi,
                springDocConfigProperties, validator);

        var examplesCustomizer = isExamplesCustomizer(customizer);

        assertThat(examplesCustomizer.getDefaultProducesMediaType()).isNull();
        assertThat(examplesCustomizer.getDefaultConsumesMediaType()).isNull();
    }

    @Test
    @DisplayName("versionCustomizer test")
    void versionCustomizerTest() {
        var version = "1.0.0";

        when(buildProperties.getVersion()).thenReturn(version);

        var openApi = new OpenAPI().info(new Info());
        var customizer = autoConfiguration.versionCustomizer(buildProperties);

        customizer.customise(openApi);

        assertThat(openApi.getInfo().getVersion()).isEqualTo(version);
    }

    @Test
    @DisplayName("versionCustomizer without Info test")
    void versionCustomizerWithNullInfoTest() {
        var version = "1.0.0";

        when(buildProperties.getVersion()).thenReturn(version);

        var openApi = new OpenAPI();
        var customizer = autoConfiguration.versionCustomizer(buildProperties);

        customizer.customise(openApi);

        assertThat(openApi.getInfo()).isNotNull();
        assertThat(openApi.getInfo().getVersion()).isEqualTo(version);
    }

    private void stubConfigurationProperties(Set<String> packagesToScan, boolean validate,
                                             boolean includeDefaultMediaTypes) {
        when(configurationProperties.getExamples().getPackagesToScan()).thenReturn(packagesToScan);
        when(configurationProperties.getExamples().isValidateExamples()).thenReturn(validate);
        when(configurationProperties.getExamples().isIncludeDefaultConsumesMediaType())
                .thenReturn(includeDefaultMediaTypes);
        when(configurationProperties.getExamples().isIncludeDefaultProducesMediaType())
                .thenReturn(includeDefaultMediaTypes);
    }

    private void stubSpringDocConfigProperties(String mediaType) {
        when(springDocConfigProperties.getDefaultConsumesMediaType()).thenReturn(mediaType);
        when(springDocConfigProperties.getDefaultProducesMediaType()).thenReturn(mediaType);
    }

    private void stubGroupedOpenApi(String group) {
        when(groupedOpenApi.getGroup()).thenReturn(group);
    }

    private ExamplesOpenApiCustomizer isExamplesCustomizer(OpenApiCustomizer openApiCustomizer) {
        assertThat(openApiCustomizer).isInstanceOf(ExamplesOpenApiCustomizer.class);
        return (ExamplesOpenApiCustomizer) openApiCustomizer;
    }
*/}