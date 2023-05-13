package io.funky.fangs.springdoc.customizer.processor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.FileObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static javax.tools.StandardLocation.CLASS_OUTPUT;

@SupportedAnnotationTypes("io.funky.fangs.springdoc.customizer.annotation.ExampleDetails")
@SupportedSourceVersion(SourceVersion.RELEASE_19)
public class ExamplesProcessor extends AbstractProcessor {
    private static final Set<Modifier> EXAMPLE_MODIFIERS = Sets.immutableEnumSet(Modifier.STATIC, Modifier.FINAL);

    public static final String EXAMPLES_FILE_NAME = "examples.json";
    public static final TypeReference<Map<String, Set<String>>> EXAMPLES_MAP_TYPE_REFERENCE = new TypeReference<>() {};
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private FileObject examplesResourceFile;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        try {
            examplesResourceFile = processingEnv.getFiler()
                    .createResource(CLASS_OUTPUT, getClass().getPackageName(), EXAMPLES_FILE_NAME,
                            processingEnv.getElementUtils().getTypeElement(getClass().getCanonicalName()));
        }
        catch (IOException ioException) {
            processingEnv.getMessager().printError(ioException.getMessage());
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // Read map from file
        Map<String, Set<String>> elements;
        try {
            elements = OBJECT_MAPPER.readValue(examplesResourceFile.getCharContent(false).toString(),
                    EXAMPLES_MAP_TYPE_REFERENCE);
        }
        catch (IllegalStateException | IOException exception) {
            // Map is likely not serialized yet, so create empty one
            elements = new HashMap<>();
        }

        // Add fields to map
        for (var annotation : annotations) {
            for (var element : roundEnv.getElementsAnnotatedWith(annotation)) {
                if (element.getModifiers().containsAll(EXAMPLE_MODIFIERS)
                        && element instanceof VariableElement fieldElement
                        && fieldElement.getEnclosingElement() instanceof TypeElement classElement) {
                    elements.computeIfAbsent(classElement.getQualifiedName().toString(), ignored -> new HashSet<>())
                            .add(fieldElement.getSimpleName().toString());
                }
            }
        }

        // Write map to file
        try (var outputStream = examplesResourceFile.openOutputStream()) {
            outputStream.write(OBJECT_MAPPER.writeValueAsBytes(elements));
        }
        catch (IOException ignored) {
        }

        return true;
    }
}