package io.funky.fangs.springdoc.customizer.utility;

import io.funky.fangs.springdoc.customizer.annotation.ExampleMethod;
import io.funky.fangs.springdoc.customizer.annotation.ExampleType;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExampleUtilitiesTest {
    @Mock
    private ExampleMethod exampleMethod;

    @Mock
    private ExampleType exampleType;

    /* ====================================== *\
     *  isRequestExample & isResponseExample  *
    \* ====================================== */

    @ParameterizedTest
    @CsvSource({
            "REQUEST, true",
            "RESPONSE, false"
    })
    void isRequestExampleTest(ExampleType.Type type, boolean result) {
        when(exampleMethod.types()).thenReturn(new ExampleType[]{exampleType});
        when(exampleType.value()).thenReturn(type);

        assertThat(ExampleUtilities.isRequestExample(exampleMethod)).isEqualTo(result);
    }

    @ParameterizedTest
    @CsvSource({
            "REQUEST, false",
            "RESPONSE, true"
    })
    void isResponseExampleTest(ExampleType.Type type, boolean result) {
        when(exampleMethod.types()).thenReturn(new ExampleType[]{exampleType});
        when(exampleType.value()).thenReturn(type);

        assertThat(ExampleUtilities.isResponseExample(exampleMethod)).isEqualTo(result);
    }

    /* ======================================= *\
     *  hasRequestParameter & hasResponseType  *
    \* ======================================= */

    @SuppressWarnings("unused") public void request(@RequestBody String ignored) {}
    @SuppressWarnings("unused") public void requestWithoutType(@RequestBody Long ignored) {}
    @SuppressWarnings("unused") public void requestWithoutAnnotation(String ignored) {}

    @ParameterizedTest
    @CsvSource({
            "request, true",
            "requestWithoutType, false",
            "requestWithoutAnnotation, false"
    })
    void hasRequestParameterTest(String methodName, boolean result) {
        Stream.of(getClass().getMethods())
                .filter(method -> method.getName().equals(methodName))
                .findAny()
                .ifPresentOrElse(method -> assertThat(ExampleUtilities.hasRequestParameter(method, String.class))
                                .isEqualTo(result),
                        () -> fail("Method was not found"));
    }

    @SuppressWarnings("unused") public String response() {return null;}
    @SuppressWarnings("unused") public Long responseWithoutType() {return null;}

    @ParameterizedTest
    @CsvSource({
            "response, true",
            "responseWithoutType, false"
    })
    void hasResponseTypeTest(String methodName, boolean result) {
        Stream.of(getClass().getMethods())
                .filter(method -> method.getName().equals(methodName))
                .findAny()
                .ifPresentOrElse(method -> assertThat(ExampleUtilities.hasResponseType(method, String.class))
                                .isEqualTo(result),
                        () -> fail("Method was not found"));
    }

    /* ============= *\
     *  getContents  *
    \* ============= */

    @Test
    void getContentsRequestTest() {
        when(exampleType.value()).thenReturn(ExampleType.Type.REQUEST);

        var content = new Content();
        var operation = new Operation()
                .requestBody(new io.swagger.v3.oas.models.parameters.RequestBody()
                        .content(content));

        assertThat(ExampleUtilities.getContents(exampleType, operation))
                .singleElement()
                .isSameAs(content);
    }

    @Test
    void getContentsResponseTest() {
        when(exampleType.value()).thenReturn(ExampleType.Type.RESPONSE);
        when(exampleType.responses()).thenReturn(new HttpStatus[]{HttpStatus.OK});

        var content = new Content();
        var operation = new Operation()
                .responses(new ApiResponses()
                        .addApiResponse(Integer.toString(HttpStatus.OK.value()), new ApiResponse()
                                .content(content)));

        assertThat(ExampleUtilities.getContents(exampleType, operation))
                .singleElement()
                .isSameAs(content);
    }

    /* =============== *\
     *  getMediaTypes  *
    \* =============== */

    static Stream<Arguments> getMediaTypesTestArguments() {
        return Stream.of(Arguments.of(new String[]{"application/json"}, null, null, Set.of("application/json")),
                Arguments.of(new String[0], "application/json", "application/json", Set.of("application/json")),
                Arguments.of(new String[0], null, null, emptySet()));
    }

    @ParameterizedTest
    @MethodSource("getMediaTypesTestArguments")
    void getMediaTypesTest(String[] exampleTypes, String defaultConsumes, String defaultProduces,
                           Collection<String> expected) {
        when(exampleType.mediaTypes()).thenReturn(exampleTypes);

        assertThat(ExampleUtilities.getMediaTypes(exampleType, defaultConsumes, defaultProduces))
                .isEqualTo(expected);
    }
}