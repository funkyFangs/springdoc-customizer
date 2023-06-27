package io.funky.fangs.springdoc.customizer.processor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Suppliers;
import com.google.common.collect.Sets;
import io.funky.fangs.springdoc.customizer.annotation.ExampleDetails;
import io.funky.fangs.springdoc.customizer.model.ExampleDetailsRecord;
import io.funky.fangs.springdoc.customizer.model.ExampleMethodRecord;
import io.funky.fangs.springdoc.customizer.model.ExampleTargetRecord;
import io.funky.fangs.springdoc.customizer.model.ExampleTypeRecord;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.tools.FileObject;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Supplier;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static javax.tools.StandardLocation.CLASS_OUTPUT;

@SupportedAnnotationTypes("io.funky.fangs.springdoc.customizer.annotation.ExampleDetails")
@SupportedSourceVersion(SourceVersion.RELEASE_19)
public class ExamplesProcessor extends AbstractProcessor {
    private static final Set<Modifier> EXAMPLE_MODIFIERS = Sets.immutableEnumSet(Modifier.STATIC, Modifier.FINAL);
    @VisibleForTesting
    static final String EXAMPLES_FILE_NAME = "examples.json";
    private static final TypeReference<Map<String, Map<String, ExampleDetailsRecord>>> EXAMPLES_MAP_TYPE_REFERENCE = new TypeReference<>() {};
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .setSerializationInclusion(NON_EMPTY);
    private static final Supplier<Map<Field, ExampleDetailsRecord>> EXAMPLES_MAP_SUPPLIER = Suppliers.memoize(ExamplesProcessor::constructExamplesMap);

    private FileObject examplesResourceFile;

    private FileObject getExamplesResourceFile() {
        if (examplesResourceFile == null) {
            try {
                examplesResourceFile = processingEnv.getFiler()
                        .createResource(CLASS_OUTPUT, getClass().getPackageName(), EXAMPLES_FILE_NAME,
                                processingEnv.getElementUtils().getTypeElement(getClass().getCanonicalName()));
            }
            catch (IOException ioException) {
                throw new IllegalStateException("Unable to create examples file", ioException);
            }
        }

        return examplesResourceFile;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!annotations.isEmpty()) {
            var examplesResourceFile = getExamplesResourceFile();

            // Read map from file
            Map<String, Map<String, ExampleDetailsRecord>> elements;
            try {
                elements = OBJECT_MAPPER.readValue(examplesResourceFile.getCharContent(false).toString(),
                        EXAMPLES_MAP_TYPE_REFERENCE);
            } catch (IllegalStateException | IOException exception) {
                // Map is likely not serialized yet, so create empty one
                elements = new HashMap<>();
            }

            var originalState = elements.entrySet()
                    .stream()
                    .collect(toUnmodifiableMap(Entry::getKey, entry -> Map.copyOf(entry.getValue())));

            // Add fields to map
            for (var annotation : annotations) {
                for (var element : roundEnv.getElementsAnnotatedWith(annotation)) {
                    if (element.getModifiers().containsAll(EXAMPLE_MODIFIERS)
                            && element instanceof VariableElement fieldElement
                            && fieldElement.getEnclosingElement() instanceof TypeElement classElement) {
                        var detailsRecord = convertToRecord(fieldElement.getAnnotation(ExampleDetails.class));

                        elements.computeIfAbsent(classElement.getQualifiedName().toString(), ignored -> new HashMap<>())
                                .put(fieldElement.getSimpleName().toString(), detailsRecord);
                    }
                }
            }

            if (!elements.equals(originalState)) {
                // Write map to file
                try (var outputStream = examplesResourceFile.openOutputStream()) {
                    outputStream.write(OBJECT_MAPPER.writeValueAsBytes(elements));
                } catch (IOException ignored) {
                    return false;
                }
            }
        }

        return true;
    }

    private static Map<Field, ExampleDetailsRecord> constructExamplesMap() {
        try {
            var examplesMap = OBJECT_MAPPER.readValue(ExamplesProcessor.class.getResource(EXAMPLES_FILE_NAME),
                    EXAMPLES_MAP_TYPE_REFERENCE);

            var result = new HashMap<Field, ExampleDetailsRecord>();
            for (var entry : examplesMap.entrySet()) {
                try {
                    var examplesClass = Class.forName(entry.getKey());

                    var exampleField = entry.getValue();
                    for (var exampleEntry : exampleField.entrySet()) {
                        try {
                            var field = examplesClass.getDeclaredField(exampleEntry.getKey());
                            var details = exampleEntry.getValue();

                            result.put(field, details);
                        }
                        catch (NoSuchFieldException ignored) {
                            // Field could not be found, so move on
                        }
                    }
                }
                catch (ClassNotFoundException ignored) {
                    // Class could not be found, so just move on
                }
            }

            return Map.copyOf(result);
        }
        catch (IOException | IllegalArgumentException ignored) {
            return emptyMap();
        }
    }

    public static Map<Field, ExampleDetailsRecord> getExampleFields() {
        return EXAMPLES_MAP_SUPPLIER.get();
    }

    private ExampleDetailsRecord convertToRecord(ExampleDetails details) {
        var targetRecords = new ArrayList<ExampleTargetRecord>();

        for (var target : details.targets()) {
            var methodRecords = new ArrayList<ExampleMethodRecord>();

            for (var method : target.methods()) {
                var typeRecords = new ArrayList<ExampleTypeRecord>();

                for (var type : method.types()) {
                    var exampleType = type.value();

                    typeRecords.add(new ExampleTypeRecord(exampleType, List.of(type.mediaTypes()),
                            List.of(type.responses())));
                }

                methodRecords.add(new ExampleMethodRecord(method.name(), typeRecords));
            }

            try {
                //noinspection ResultOfMethodCallIgnored
                target.controller();
            }
            catch (MirroredTypeException exception) {
                var controller = (TypeElement) processingEnv.getTypeUtils().asElement(exception.getTypeMirror());

                targetRecords.add(new ExampleTargetRecord(controller.getQualifiedName().toString(), methodRecords));
            }
        }

        return new ExampleDetailsRecord(details.name(), details.summary(), details.description(), targetRecords);
    }
}