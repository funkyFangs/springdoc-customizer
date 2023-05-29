package io.funky.fangs.springdoc.customizer.processor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

import java.io.IOException;

import static io.funky.fangs.springdoc.customizer.processor.ExamplesProcessor.EXAMPLES_FILE_NAME;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ExamplesProcessorTest {
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ProcessingEnvironment processingEnvironment;

    private ExamplesProcessor examplesProcessor;

    @BeforeEach
    void setUp() {
        examplesProcessor = new ExamplesProcessor();
    }

    @Test
    void initTest() throws IOException {
        examplesProcessor.init(processingEnvironment);

        verify(processingEnvironment.getFiler())
                .createResource(eq(CLASS_OUTPUT), eq(ExamplesProcessor.class.getPackageName()),
                        eq(EXAMPLES_FILE_NAME), any(TypeElement.class));
    }
}