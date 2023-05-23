package io.funky.fangs.springdoc.customizer.utility;

import io.funky.fangs.springdoc.customizer.annotation.ExampleType.Type;
import io.funky.fangs.springdoc.customizer.model.ExampleMethodRecord;
import io.funky.fangs.springdoc.customizer.model.ExampleTypeRecord;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.responses.ApiResponse;
import jakarta.annotation.Nullable;
import lombok.experimental.UtilityClass;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
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
    private boolean isRequestExample(ExampleMethodRecord method) {
        return isExampleOfType(method, Type.REQUEST);
    }

    private boolean isResponseExample(ExampleMethodRecord method) {
        return isExampleOfType(method, Type.RESPONSE);
    }

    private boolean isExampleOfType(ExampleMethodRecord method, Type type) {
        return method.types()
                .stream()
                .map(ExampleTypeRecord::type)
                .anyMatch(type::equals);
    }

    private boolean hasRequestParameter(Method method, Class<?> exampleType) {
        return Stream.of(method.getParameters())
                .anyMatch(parameter -> exampleType.isAssignableFrom(parameter.getType())
                        && parameter.isAnnotationPresent(RequestBody.class));
    }

    private boolean hasResponseType(Method method, Class<?> exampleType) {
        return exampleType.isAssignableFrom(method.getReturnType());
    }

    public Map<ExampleMethodRecord, Collection<Method>> getControllerMethods(Class<?> controller, Class<?> exampleType,
                                                                             Collection<ExampleMethodRecord> exampleMethods) {
        var exampleMethodsByName = exampleMethods.stream()
                .collect(toMap(ExampleMethodRecord::name, Function.identity()));

        var result = new HashMap<ExampleMethodRecord, Collection<Method>>();
        for (var method : controller.getDeclaredMethods()) {
            var name = method.getName();

            Optional.ofNullable(exampleMethodsByName.get(name))
                    .filter(exampleMethod -> isRequestExample(exampleMethod) && hasRequestParameter(method, exampleType)
                            || isResponseExample(exampleMethod) && hasResponseType(method, exampleType))
                    .ifPresent(exampleMethod -> result.computeIfAbsent(exampleMethod, ignored -> new ArrayList<>())
                            .add(method));
        }

        return result;
    }

    public Collection<String> getMediaTypes(ExampleTypeRecord type, @Nullable String defaultConsumesMediaType,
                                            @Nullable String defaultProducesMediaType) {
        return Stream.of(type.mediaTypes().stream(),
                        Stream.ofNullable(defaultConsumesMediaType).filter(ignored -> type.type() == Type.REQUEST),
                        Stream.ofNullable(defaultProducesMediaType).filter(ignored -> type.type() == Type.RESPONSE))
                .flatMap(Function.identity())
                .collect(toSet());
    }

    public Collection<Content> getContents(ExampleTypeRecord type, Operation operation) {
        return switch (type.type()) {
            case REQUEST -> Optional.ofNullable(operation)
                    .map(Operation::getRequestBody)
                    // Qualified because Spring's @RequestBody is imported
                    .map(io.swagger.v3.oas.models.parameters.RequestBody::getContent)
                    .map(List::of)
                    .orElse(emptyList());
            case RESPONSE -> type.responses()
                    .stream()
                    .map(HttpStatus::value)
                    .map(Object::toString)
                    .map(operation.getResponses()::get)
                    .filter(Objects::nonNull)
                    .map(ApiResponse::getContent)
                    .filter(Objects::nonNull)
                    .toList();
        };
    }

    private final String ROOT_PATH = "/";

    public RequestMapping getRequestMapping(AnnotatedElement annotatedElement) {
        return AnnotatedElementUtils.findMergedAnnotation(annotatedElement, RequestMapping.class);
    }

    public Collection<String> getRequestMappingPaths(AnnotatedElement annotatedElement) {
        return getRequestMappingPaths(getRequestMapping(annotatedElement));
    }

    public Collection<String> getRequestMappingPaths(RequestMapping requestMapping) {
        return Optional.ofNullable(requestMapping)
                .map(RequestMapping::path)
                .filter(paths -> paths.length > 0)
                .map(Set::of)
                .orElseGet(() -> Set.of(""));
    }

    public String normalizePath(String path) {
        return path.isEmpty() ? ROOT_PATH : path;
    }
}