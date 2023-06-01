package io.funky.fangs.springdoc.customizer.utility;

import lombok.experimental.UtilityClass;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

@UtilityClass
public class ResourceUtilities {
    public final int LARGE_ICON_SIZE = 32;
    public final int SMALL_ICON_SIZE = 16;
    private final String ICON_FORMAT = "(<link rel=\"icon\" type=\"image/png\" href=\")"
            + "./favicon-%dx%d.png"
            + "(\" sizes=\"%dx%d\" />)";
    public final Pattern SMALL_ICON_PATTERN = getIconPattern(SMALL_ICON_SIZE);
    public final Pattern LARGE_ICON_PATTERN = getIconPattern(LARGE_ICON_SIZE);
    public final Pattern TITLE_PATTERN = Pattern.compile("(<title>)Swagger UI(</title>)");
    public final String INDEX_FILE = "index.html";
    public final String ROOT_PATH = "/";

    private final Path BASE_PATH;

    static {
        Path path;
        try {
            path = Paths.get(new ClassPathResource("static").getURI()).normalize();
        } catch (IOException e) {
            path = null;
        }
        BASE_PATH = path;
    }

    private Pattern getIconPattern(int size) {
        return Pattern.compile(ICON_FORMAT.formatted(size, size, size, size));
    }

    public String replaceLargeIcon(String document, String largeIconPath) {
        return replaceIcon(document, largeIconPath, LARGE_ICON_PATTERN);
    }

    public String replaceSmallIcon(String document, String smallIconPath) {
        return replaceIcon(document, smallIconPath, SMALL_ICON_PATTERN);
    }

    private String replaceIcon(String document, String iconPath, Pattern pattern) {
        return replaceEnclosedToken(document, iconPath, pattern);
    }

    public String replaceTitle(String document, String title) {
        return replaceEnclosedToken(document, title, TITLE_PATTERN);
    }

    private String replaceEnclosedToken(String document, String replacement, Pattern pattern) {
        return pattern.matcher(document)
                .replaceFirst(matchResult -> matchResult.group(1) + replacement + matchResult.group(2));
    }

    public String resolvePath(String pathName) {
        // Resolve and normalize path from base path
        var resolvedPath = BASE_PATH.resolve(pathName).normalize();

        // Ensure that normalized path is still within scope, and that the path points to an existing file
        if (resolvedPath.startsWith(BASE_PATH) && resolvedPath.toFile().exists()) {
            // Make path relative to base path; this simplifies redundant traversals within the given path
            // For example: "../static/icon.png" -> "icon.png"
            return BASE_PATH.relativize(resolvedPath).toString();
        }
        else {
            return null;
        }
    }
}