package io.funky.fangs.springdoc.customizer.configuration;

import com.google.common.annotations.VisibleForTesting;
import io.funky.fangs.springdoc.customizer.annotation.ExampleDetails;
import io.funky.fangs.springdoc.customizer.model.ExampleDetailsRecord;
import io.funky.fangs.springdoc.customizer.model.ExampleTargetRecord;
import io.funky.fangs.springdoc.customizer.model.ExampleTypeRecord;
import io.funky.fangs.springdoc.customizer.processor.ExamplesProcessor;
import io.funky.fangs.springdoc.customizer.utility.ExampleUtilities;
import io.funky.fangs.springdoc.customizer.utility.ReflectionUtilities;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.Content;
import jakarta.validation.Validator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
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
    private final String group;
    private final String defaultConsumesMediaType;
    private final String defaultProducesMediaType;
    private final Validator validator;
    private final Map<Field, ExampleDetailsRecord> exampleFields = ExamplesProcessor.getExampleFields();

    /**
     * @param group the group of a grouped REST API, or null
     * @param validator the {@link Validator} used to validate examples, or null
     */
    public ExamplesOpenApiCustomizer(String group,
                                     String defaultConsumesMediaType,
                                     String defaultProducesMediaType,
                                     Validator validator) {
        this.group = Optional.ofNullable(group).orElse("");
        this.defaultConsumesMediaType = defaultConsumesMediaType;
        this.defaultProducesMediaType = defaultProducesMediaType;
        this.validator = validator;
    }

    @Override
    public void customise(OpenAPI openApi) {
        var paths = openApi.getPaths();

        if (paths != null)
            for (var entry : exampleFields.entrySet()) {
                var value = ReflectionUtilities.getFieldValue(entry.getKey());

                if (isValid(value)) {
                    var exampleDetails = entry.getValue();
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

    private void injectExample(NamedExample example, ExampleTargetRecord target, Paths specificationPaths) {
        var exampleClass = example.getValue().getClass();
        var controller = ReflectionUtilities.getClassSafely(target.controller());
        var controllerPaths = ExampleUtilities.getRequestMappingPaths(controller)
                .stream()
                .map(group::concat)
                .distinct()
                .toList();

        var exampleMethods = ExampleUtilities.getControllerMethods(controller, exampleClass, target.methods());

        for (var entry : exampleMethods.entrySet()) {
            var methodRequestMappings = Optional.ofNullable(entry.getValue())
                    .orElse(emptyList())
                    .stream()
                    .map(ExampleUtilities::getRequestMapping)
                    .toList();

            for (var methodRequestMapping : methodRequestMappings) {
                var paths = ExampleUtilities.getRequestMappingPaths(methodRequestMapping).stream()
                        .flatMap(methodPath -> controllerPaths.stream()
                                .map(controllerPath -> controllerPath + methodPath))
                        .distinct()
                        .map(ExampleUtilities::normalizePath)
                        .toList();

                paths.stream()
                        .map(specificationPaths::get)
                        .filter(Objects::nonNull)
                        .forEach(pathItem -> injectIntoPath(example, pathItem, entry.getKey().types(),
                                methodRequestMapping.method()));
            }
        }
    }

    private void injectIntoPath(NamedExample example, PathItem pathItem, Collection<ExampleTypeRecord> exampleTypes,
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
                    .map(operation -> ExampleUtilities.getContents(exampleType, operation))
                    .flatMap(Collection::stream)
                    .forEach(content -> injectIntoContent(example, content, exampleType));
    }

    private void injectIntoContent(NamedExample example, Content content, ExampleTypeRecord exampleType) {
        for (var mediaTypeValue : ExampleUtilities.getMediaTypes(exampleType, defaultConsumesMediaType,
                defaultProducesMediaType)) {
            Optional.ofNullable(content.get(mediaTypeValue))
                    .ifPresent(mediaType -> mediaType.addExamples(example.getName(), example));
        }
    }
}