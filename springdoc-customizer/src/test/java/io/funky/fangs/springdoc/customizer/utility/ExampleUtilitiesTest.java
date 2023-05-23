package io.funky.fangs.springdoc.customizer.utility;

import io.funky.fangs.springdoc.customizer.model.ExampleTypeRecord;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Stream;

import static io.funky.fangs.springdoc.customizer.annotation.ExampleType.Type.REQUEST;
import static io.funky.fangs.springdoc.customizer.annotation.ExampleType.Type.RESPONSE;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;

class ExampleUtilitiesTest {
    /* ============= *\
     *  getContents  *
    \* ============= */

    @Test
    void getContentsRequestTest() {
        var exampleType = new ExampleTypeRecord(REQUEST, null, null);

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
        var exampleType = new ExampleTypeRecord(RESPONSE, null, EnumSet.of(HttpStatus.OK));

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
        return Stream.of(Arguments.of(new ExampleTypeRecord(null, Set.of("application/json"), null),
                        null, null, Set.of("application/json")),
                Arguments.of(new ExampleTypeRecord(REQUEST, null, null),
                        "application/json", null, Set.of("application/json")),
                Arguments.of(new ExampleTypeRecord(REQUEST, null, null),
                        null, "application/json", emptySet()),
                Arguments.of(new ExampleTypeRecord(RESPONSE, null, null),
                        null, "application/json", Set.of("application/json")),
                Arguments.of(new ExampleTypeRecord(RESPONSE, null, null),
                        "application/json", null, emptySet()),
                Arguments.of(new ExampleTypeRecord(null, null, null),
                        null, null, emptySet()));
    }

    @ParameterizedTest
    @MethodSource("getMediaTypesTestArguments")
    void getMediaTypesTest(ExampleTypeRecord exampleType, String defaultConsumes, String defaultProduces,
                           Collection<String> expected) {
        assertThat(ExampleUtilities.getMediaTypes(exampleType, defaultConsumes, defaultProduces))
                .isEqualTo(expected);
    }
}