package io.funky.fangs.springdoc.customizer.configuration;

import io.funky.fangs.springdoc.customizer.annotations.ExampleDetails;
import io.funky.fangs.springdoc.customizer.annotations.ExampleMethod;
import io.funky.fangs.springdoc.customizer.annotations.ExampleTarget;
import io.funky.fangs.springdoc.customizer.annotations.ExampleType;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import jakarta.validation.Validator;
import lombok.Builder;
import lombok.Data;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static io.funky.fangs.springdoc.customizer.annotations.ExampleType.Type.REQUEST;
import static io.funky.fangs.springdoc.customizer.annotations.ExampleType.Type.RESPONSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@ExtendWith(MockitoExtension.class)
class ExamplesOpenApiCustomizerTest {
    @Data
    @Builder
    static class TestPojo {
        private String string;
    }

    @SuppressWarnings("unused") // Methods are used for testing customizer
    @RequestMapping("/test")
    static class TestController {
        @PostMapping
        public void request(@org.springframework.web.bind.annotation.RequestBody TestPojo ignored) {
        }

        @GetMapping("/{string}")
        public TestPojo response(@PathVariable String string) {
            return new TestPojo(string);
        }
    }

    @ExampleDetails(name = "Example",
            summary = "Example",
            description = "An example for testing",
            targets = @ExampleTarget(controller = TestController.class,
                    methods = {
                            @ExampleMethod(name = "request", types = @ExampleType(REQUEST)),
                            @ExampleMethod(name = "response", types = @ExampleType(value = RESPONSE, responses = OK))
                    }))
    static final TestPojo EXAMPLE = TestPojo.builder()
            .string("Example")
            .build();

    @Mock(answer = RETURNS_DEEP_STUBS)
    private Validator validator;

    private ExamplesOpenApiCustomizer customizer;

    @Test
    @DisplayName("customize test")
    void customizeTest() {
        var group = "/group";
        var mediaType = APPLICATION_JSON_VALUE;
        var packagesToScan = Set.of(getClass().getPackageName());

        setUp(group, mediaType, mediaType, true, packagesToScan);

        var openApi = getOpenApi(group);

        customizer.customise(openApi);

        var requestExamples = getRequestExamples(openApi, mediaType, group);

        assertThat(requestExamples).containsKey("Example");
        assertThat(requestExamples.get("Example").getValue()).isSameAs(EXAMPLE);

        var responseExamples = getResponseExamples(openApi, mediaType, OK, group);

        assertThat(responseExamples).containsKey("Example");
        assertThat(responseExamples.get("Example").getValue()).isSameAs(EXAMPLE);
    }

    @Test
    @DisplayName("customize with null group test")
    void customizeWithNullGroupTest() {
        var mediaType = APPLICATION_JSON_VALUE;
        var packagesToScan = Set.of(getClass().getPackageName());

        setUp(null, mediaType, mediaType, true, packagesToScan);

        var openApi = getOpenApi();

        customizer.customise(openApi);

        var requestExamples = getRequestExamples(openApi, mediaType);

        assertThat(requestExamples).containsKey("Example");
        assertThat(requestExamples.get("Example").getValue()).isSameAs(EXAMPLE);

        var responseExamples = getResponseExamples(openApi, mediaType, OK);

        assertThat(responseExamples).containsKey("Example");
        assertThat(responseExamples.get("Example").getValue()).isSameAs(EXAMPLE);
    }

    @Test
    @DisplayName("customize with null Validator test")
    void customizeWithNullValidatorTest() {
        var group = "/group";
        var mediaType = APPLICATION_JSON_VALUE;
        var packagesToScan = Set.of(getClass().getPackageName());

        setUp(group, mediaType, mediaType, true, packagesToScan);

        var openApi = getOpenApi(group);

        customizer.customise(openApi);

        var requestExamples = getRequestExamples(openApi, mediaType, group);

        assertThat(requestExamples).containsKey("Example");
        assertThat(requestExamples.get("Example").getValue()).isSameAs(EXAMPLE);

        var responseExamples = getResponseExamples(openApi, mediaType, OK, group);

        assertThat(responseExamples).containsKey("Example");
        assertThat(responseExamples.get("Example").getValue()).isSameAs(EXAMPLE);
    }

    @Test
    @DisplayName("customize with invalid example test")
    void customizeWithInvalidExampleTest() {
        var group = "/group";
        var mediaType = APPLICATION_JSON_VALUE;
        var packagesToScan = Set.of(getClass().getPackageName());

        setUp(group, mediaType, mediaType, false, packagesToScan);

        var openApi = getOpenApi(group);

        customizer.customise(openApi);

        assertThat(getRequestExamples(openApi, mediaType, group)).isNull();
        assertThat(getResponseExamples(openApi, mediaType, OK, group)).isNull();
    }

    @Test
    @DisplayName("customize with null default media types test")
    void customizeWithNullDefaultMediaTypesTest() {
        var group = "/group";
        var packagesToScan = Set.of(getClass().getPackageName());

        setUp(group, null, null, false, packagesToScan);

        var openApi = getOpenApi(group);

        customizer.customise(openApi);

        assertThat(getRequestExamples(openApi, APPLICATION_JSON_VALUE, group)).isNull();
        assertThat(getResponseExamples(openApi, APPLICATION_JSON_VALUE, OK, group)).isNull();
    }

    private OpenAPI getOpenApi(String group) {
        return new OpenAPI().paths(new Paths()
                .addPathItem(group + "/test", new PathItem().post(new Operation()
                        .requestBody(new RequestBody()
                                .content(new Content().addMediaType("application/json", new MediaType())))))
                .addPathItem(group + "/test/{string}", new PathItem().get(new Operation()
                        .responses(new ApiResponses().addApiResponse("200", new ApiResponse()
                                .content(new Content().addMediaType("application/json", new MediaType())))))));
    }

    private OpenAPI getOpenApi() {
        return getOpenApi("");
    }

    private void setUp(String group, String defaultConsumesMediaType, String defaultProducesMediaType,
                       boolean isValid, Collection<String> packagesToScan) {
        customizer = new ExamplesOpenApiCustomizer(group, defaultConsumesMediaType, defaultProducesMediaType, validator,
                packagesToScan);

        when(validator.validate(EXAMPLE).isEmpty()).thenReturn(isValid);
    }

    private Map<String, Example> getRequestExamples(OpenAPI openApi, String mediaType, String group) {
        return openApi.getPaths()
                .get(group + "/test")
                .getPost()
                .getRequestBody()
                .getContent()
                .get(mediaType)
                .getExamples();
    }

    private Map<String, Example> getRequestExamples(OpenAPI openApi, String mediaType) {
        return getRequestExamples(openApi, mediaType, "");
    }

    private Map<String, Example> getResponseExamples(OpenAPI openApi, String mediaType, HttpStatus status, String group) {
        return openApi.getPaths()
                .get(group + "/test/{string}")
                .getGet()
                .getResponses()
                .get(Integer.toString(status.value()))
                .getContent()
                .get(mediaType)
                .getExamples();
    }

    private Map<String, Example> getResponseExamples(OpenAPI openApi, String mediaType, HttpStatus status) {
        return getResponseExamples(openApi, mediaType, status, "");
    }
}