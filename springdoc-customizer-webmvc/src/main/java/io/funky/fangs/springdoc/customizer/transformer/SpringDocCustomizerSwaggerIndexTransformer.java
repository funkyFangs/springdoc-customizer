package io.funky.fangs.springdoc.customizer.transformer;

import io.funky.fangs.springdoc.customizer.utility.ResourceUtilities;
import jakarta.servlet.http.HttpServletRequest;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.webmvc.ui.SwaggerIndexPageTransformer;
import org.springdoc.webmvc.ui.SwaggerWelcomeCommon;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.TransformedResource;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.util.Optional;

public class SpringDocCustomizerSwaggerIndexTransformer extends SwaggerIndexPageTransformer {
    private final String title;
    private final String largeIconPath;
    private final String smallIconPath;

    /**
     * Instantiates a new Swagger index transformer.
     *
     * @param title                     the title of the Swagger UI
     * @param largeIconPath             the path to the large Swagger UI icon
     * @param smallIconPath             the path to the small Swagger UI icon
     * @param swaggerUiConfig           the swagger ui config
     * @param swaggerUiOAuthProperties  the swagger ui o auth properties
     * @param swaggerUiConfigParameters the swagger ui config parameters
     * @param swaggerWelcomeCommon      the swagger welcome common
     * @param objectMapperProvider      the object mapper provider
     */
    public SpringDocCustomizerSwaggerIndexTransformer(String title, String largeIconPath, String smallIconPath,
                                                      SwaggerUiConfigProperties swaggerUiConfig,
                                                      SwaggerUiOAuthProperties swaggerUiOAuthProperties,
                                                      SwaggerUiConfigParameters swaggerUiConfigParameters,
                                                      SwaggerWelcomeCommon swaggerWelcomeCommon,
                                                      ObjectMapperProvider objectMapperProvider) {
        super(swaggerUiConfig, swaggerUiOAuthProperties, swaggerUiConfigParameters, swaggerWelcomeCommon,
                objectMapperProvider);
        this.title = HtmlUtils.htmlEscape(title);
        this.largeIconPath = Optional.ofNullable(largeIconPath)
                .map(ResourceUtilities::resolvePath)
                .map(HtmlUtils::htmlEscape)
                .map(ResourceUtilities.ROOT_PATH::concat)
                .orElse(null);
        this.smallIconPath = Optional.ofNullable(smallIconPath)
                .map(ResourceUtilities::resolvePath)
                .map(HtmlUtils::htmlEscape)
                .map(ResourceUtilities.ROOT_PATH::concat)
                .orElse(null);
    }

    @Override
    public Resource transform(HttpServletRequest request, Resource resource,
                              ResourceTransformerChain transformerChain) throws IOException {
        if (resource.toString().contains(ResourceUtilities.INDEX_FILE)) {
            var document = readFullyAsString(resource.getInputStream());

            if (title != null) {
                document = ResourceUtilities.replaceTitle(document, title);
            }

            var replaceLargeIcon = largeIconPath != null;
            var replaceSmallIcon = smallIconPath != null;

            if (replaceLargeIcon) {
                document = ResourceUtilities.replaceLargeIcon(document, largeIconPath);
                if (!replaceSmallIcon) {
                    document = ResourceUtilities.SMALL_ICON_PATTERN
                            .matcher(document)
                            .replaceFirst("");
                }
            }

            if (replaceSmallIcon) {
                document = ResourceUtilities.replaceSmallIcon(document, smallIconPath);
                if (!replaceLargeIcon) {
                    document = ResourceUtilities.LARGE_ICON_PATTERN
                            .matcher(document)
                            .replaceFirst("");
                }
            }

            return new TransformedResource(resource, document.getBytes());
        }

        return super.transform(request, resource, transformerChain);
    }
}