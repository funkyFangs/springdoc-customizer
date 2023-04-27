package io.funky.fangs.springdoc.customizer.utilities;

import com.google.common.collect.Multimap;
import io.funky.fangs.springdoc.customizer.annotations.ExampleMethod;
import io.funky.fangs.springdoc.customizer.annotations.ExampleType;
import io.funky.fangs.springdoc.customizer.annotations.ExampleType.Type;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.responses.ApiResponse;
import jakarta.annotation.Nullable;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.google.common.collect.ImmutableListMultimap.toImmutableListMultimap;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

/**
 * Utilities related to the processing of examples.
 *
 * @author Harper Price
 * @since 2.1.0
 */
@UtilityClass
public class ExampleUtilities {
    /**
     * Determines if an {@link ExampleMethod} has any {@link Type#REQUEST} types.

     * @param method the {@link ExampleMethod} to validate
     * @return true if any {@link Type#REQUEST } types are present
     */
    public boolean isRequestExample(ExampleMethod method) {
        return isExampleOfType(method, Type.REQUEST);
    }

    /**
     * Determines if an {@link ExampleMethod} has any {@link Type#RESPONSE} types.

     * @param method the {@link ExampleMethod} to validate
     * @return true if any {@link Type#RESPONSE} types are present
     */
    public boolean isResponseExample(ExampleMethod method) {
        return isExampleOfType(method, Type.RESPONSE);
    }

    private boolean isExampleOfType(ExampleMethod method, Type type) {
        return Stream.of(method.types())
                .map(ExampleType::value)
                .anyMatch(type::equals);
    }

    /**
     * Determines if a {@link Method} has a {@link Parameter} which matches the example type
     * and is a {@link RequestBody}.
     *
     * @param method the {@link Method} to validate
     * @param exampleType the {@link Class} of the example
     * @return true if the {@link Method} has an applicable {@link Parameter}, or else false
     */
    public boolean hasRequestParameter(Method method, Class<?> exampleType) {
        return Stream.of(method.getParameters())
                .anyMatch(parameter -> exampleType.isAssignableFrom(parameter.getType())
                        && parameter.isAnnotationPresent(RequestBody.class));
    }

    /**
     * Compares a {@link Method}'s return type against an example's type.
     *
     * @param method the {@link Method} to validate
     * @param exampleType the {@link Class} of the example.
     * @return whether example type is assignable from the return type
     */
    public boolean hasResponseType(Method method, Class<?> exampleType) {
        return exampleType.isAssignableFrom(method.getReturnType());
    }

    /**
     * Constructs a {@link Multimap} of {@link ExampleMethod}s to their corresponding {@link Method}s belonging to the
     * provided controller.
     *
     * @param controller the {@link Class} of the controller
     * @param exampleMethods the {@link ExampleMethod}s to create mappings for
     * @return a {@link Multimap} of {@link ExampleMethod}s and their corresponding {@link Method}s from the controller
     */
    public Multimap<ExampleMethod, Method> getControllerMethods(Class<?> controller, ExampleMethod[] exampleMethods) {
        var exampleMethodsByName = Stream.of(exampleMethods)
                .collect(toMap(ExampleMethod::name, Function.identity()));

        return Stream.of(controller.getMethods())
                .filter(method -> exampleMethodsByName.containsKey(method.getName()))
                .collect(toImmutableListMultimap(method -> exampleMethodsByName.get(method.getName()),
                        Function.identity()));
    }

    /**
     * Creates a {@link Collection} of {@link MediaType}s based on the types specified in the {@link ExampleType} as
     * well as the default consumes and produces media types.
     *
     * @param type the {@link ExampleType} containing the applicable {@link MediaType}s
     * @return a {@link Collection} of applicable {@link MediaType}s
     */
    public Collection<String> getMediaTypes(ExampleType type, @Nullable String defaultConsumesMediaType,
                                            @Nullable String defaultProducesMediaType) {
        return Stream.of(Stream.of(type.mediaTypes()),
                        Stream.ofNullable(defaultConsumesMediaType).filter(ignored -> type.value() == Type.REQUEST),
                        Stream.ofNullable(defaultProducesMediaType).filter(ignored -> type.value() == Type.RESPONSE))
                .flatMap(Function.identity())
                .collect(toSet());
    }

    /**
     * Get the {@link Content}s from the {@link Operation} based on the targets specified in the {@link ExampleType}.
     *
     * @param type the details of the {@link Content}s to get
     * @param operation the {@link Operation} to source {@link Content}s from
     * @return the applicable {@link Content}s of the {@link Operation}
     */
    public Collection<Content> getContents(ExampleType type, Operation operation) {
        return switch (type.value()) {
            case REQUEST -> Stream.ofNullable(operation)
                    .map(Operation::getRequestBody)
                    .filter(Objects::nonNull)
                    // Qualified because Spring's @RequestBody is imported
                    .map(io.swagger.v3.oas.models.parameters.RequestBody::getContent)
                    .toList();
            case RESPONSE -> {
                var responses = Stream.of(type.responses())
                        .map(HttpStatus::value)
                        .map(code -> Integer.toString(code))
                        .collect(toSet());

                yield Stream.ofNullable(operation)
                        .map(Operation::getResponses)
                        .flatMap(apiResponses -> responses.stream()
                                .map(apiResponses::get)
                                .map(ApiResponse::getContent)
                                .filter(Objects::nonNull))
                        .toList();
            }
        };
    }
}