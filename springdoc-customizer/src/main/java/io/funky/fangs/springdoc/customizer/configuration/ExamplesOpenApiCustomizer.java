package io.funky.fangs.springdoc.customizer.configuration;

import com.google.common.annotations.VisibleForTesting;
import io.funky.fangs.springdoc.customizer.annotations.ExampleDetails;
import io.funky.fangs.springdoc.customizer.annotations.ExampleTarget;
import io.funky.fangs.springdoc.customizer.annotations.ExampleType;
import io.funky.fangs.springdoc.customizer.utilities.RequestMappingUtilities;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.Content;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.Validator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static io.funky.fangs.springdoc.customizer.utilities.ExampleUtilities.*;
import static io.funky.fangs.springdoc.customizer.utilities.ReflectionUtilities.getFieldValue;
import static io.funky.fangs.springdoc.customizer.utilities.RequestMappingUtilities.getRequestMappingPaths;
import static lombok.AccessLevel.PACKAGE;

/**
 * A {@link OpenApiCustomizer} which injects examples annotated with {@link ExampleDetails}
 * into a given {@link OpenAPI} specification based on the provided configurations and targets.
 *
 * @author Harper Price
 * @since 2.1.0
 */
@Slf4j
@VisibleForTesting
@Getter(PACKAGE)
public class ExamplesOpenApiCustomizer implements OpenApiCustomizer {
    @Nonnull
    private final String group;

    @Nullable
    private final String defaultConsumesMediaType;

    @Nullable
    private final String defaultProducesMediaType;

    @Nullable
    private final Validator validator;

    @Nonnull
    private final Reflections reflections;

    /**
     * @param group the group of a grouped REST API, or null
     * @param validator the {@link Validator} used to validate examples, or null
     * @param packagesToScan the packages to scan for examples
     */
    public ExamplesOpenApiCustomizer(@Nullable String group,
                                     @Nullable String defaultConsumesMediaType,
                                     @Nullable String defaultProducesMediaType,
                                     @Nullable Validator validator,
                                     Collection<String> packagesToScan) {
        this.group = group == null ? "" : group;
        this.defaultConsumesMediaType = defaultConsumesMediaType;
        this.defaultProducesMediaType = defaultProducesMediaType;
        this.validator = validator;
        this.reflections = new Reflections(Objects.requireNonNull(packagesToScan), Scanners.FieldsAnnotated);
    }

    @Override
    public void customise(OpenAPI openApi) {
        var paths = openApi.getPaths();

        if (paths != null)
            for (var field : reflections.getFieldsAnnotatedWith(ExampleDetails.class)) {
                var exampleDetails = field.getAnnotation(ExampleDetails.class);
                var value = getFieldValue(field);

                if (isValid(value)) {
                    var example = new NamedExample()
                            .name(exampleDetails.name())
                            .summary(exampleDetails.summary())
                            .description(exampleDetails.description())
                            .value(value);

                    for (var target : exampleDetails.targets()) {
                        injectExample(example, target, paths);
                    }
                }
            }
    }

    private boolean isValid(Object value) {
        return value != null && (validator == null || validator.validate(value).isEmpty());
    }

    private void injectExample(NamedExample example, ExampleTarget target, Paths specificationPaths) {
        var exampleClass = example.getValue().getClass();
        var controller = target.controller();
        var controllerPaths = getRequestMappingPaths(controller)
                .stream()
                .map(group::concat)
                .distinct()
                .toList();

        var exampleMethods = getControllerMethods(controller, target.methods());

        for (var exampleMethod : exampleMethods.keySet()) {
            var methodRequestMappings = exampleMethods.get(exampleMethod)
                    .stream()
                    .filter(method -> isRequestExample(exampleMethod) && hasRequestParameter(method, exampleClass)
                            || isResponseExample(exampleMethod) && hasResponseType(method, exampleClass))
                    .map(RequestMappingUtilities::getRequestMapping)
                    .toList();

            for (var methodRequestMapping : methodRequestMappings) {
                var paths = getRequestMappingPaths(methodRequestMapping).stream()
                        .flatMap(methodPath -> controllerPaths.stream()
                                .map(controllerPath -> controllerPath + methodPath))
                        .distinct()
                        .map(RequestMappingUtilities::normalizePath)
                        .toList();

                paths.stream()
                        .map(specificationPaths::get)
                        .filter(Objects::nonNull)
                        .forEach(pathItem -> injectIntoPath(example, pathItem, exampleMethod.types(),
                                methodRequestMapping.method()));
            }
        }
    }

    private void injectIntoPath(NamedExample example, PathItem pathItem, ExampleType[] exampleTypes,
                                RequestMethod[] requestMethods) {
        var operationsMap = pathItem.readOperationsMap();

        var operations = Stream.of(requestMethods)
                .map(Enum::toString)
                .map(PathItem.HttpMethod::valueOf)
                .map(operationsMap::get)
                .filter(Objects::nonNull)
                .toList();

        for (var exampleType : exampleTypes)
            operations.stream()
                    .map(operation -> getContents(exampleType, operation))
                    .flatMap(Collection::stream)
                    .forEach(content -> injectIntoContent(example, content, exampleType));
    }

    private void injectIntoContent(NamedExample example, Content content, ExampleType exampleType) {
        for (var mediaTypeValue : getMediaTypes(exampleType, defaultConsumesMediaType, defaultProducesMediaType)) {
            Optional.ofNullable(content.get(mediaTypeValue))
                    .ifPresent(mediaType -> mediaType.addExamples(example.getName(), example));
        }
    }
}