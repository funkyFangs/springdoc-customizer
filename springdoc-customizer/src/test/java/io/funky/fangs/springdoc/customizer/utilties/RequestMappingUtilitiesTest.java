package io.funky.fangs.springdoc.customizer.utilties;

import io.funky.fangs.springdoc.customizer.utilities.RequestMappingUtilities;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class RequestMappingUtilitiesTest {
    @ParameterizedTest
    @CsvSource({
            "/test, /test",
            "'', /"
    })
    void normalizePathTest(String path, String normalizedPath) {
        assertThat(RequestMappingUtilities.normalizePath(path)).isEqualTo(normalizedPath);
    }
}