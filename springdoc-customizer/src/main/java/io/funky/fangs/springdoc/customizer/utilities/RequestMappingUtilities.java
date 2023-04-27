package io.funky.fangs.springdoc.customizer.utilities;

import lombok.experimental.UtilityClass;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * Utilities related to {@link RequestMapping}s.
 *
 * @author Harper Price
 * @since 2.1.0
 */
@UtilityClass
public class RequestMappingUtilities {
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