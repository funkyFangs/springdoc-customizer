package io.funky.fangs.springdoc.customizer.transformer;

import io.funky.fangs.springdoc.customizer.utility.ResourceUtilities;
import lombok.SneakyThrows;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.webflux.ui.SwaggerIndexPageTransformer;
import org.springdoc.webflux.ui.SwaggerWelcomeCommon;
import org.springframework.core.io.Resource;
import org.springframework.web.reactive.resource.ResourceTransformerChain;
import org.springframework.web.reactive.resource.TransformedResource;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.HtmlUtils;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.Supplier;

public class SpringDocCustomizerSwaggerIndexTransformer extends SwaggerIndexPageTransformer {
    private final String title;
    private final String largeIconPath;
    private final String smallIconPath;

    /**
     * Instantiates a new Swagger index transformer.
     *
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
        super(swaggerUiConfig, swaggerUiOAuthProperties, swaggerUiConfigParameters, swaggerWelcomeCommon, objectMapperProvider);
        this.title = HtmlUtils.htmlEscape(title);
        this.largeIconPath = Optional.ofNullable(largeIconPath)
                .map(ResourceUtilities::resolvePath)
                .map(ResourceUtilities.ROOT_PATH::concat)
                .map(HtmlUtils::htmlEscape)
                .orElse(null);
        this.smallIconPath = Optional.ofNullable(smallIconPath)
                .map(ResourceUtilities::resolvePath)
                .map(ResourceUtilities.ROOT_PATH::concat)
                .map(HtmlUtils::htmlEscape)
                .orElse(null);
    }

    @Override
    public Mono<Resource> transform(ServerWebExchange serverWebExchange, Resource resource, ResourceTransformerChain resourceTransformerChain) {
        if (resource.toString().contains(ResourceUtilities.INDEX_FILE)) {
            //noinspection Convert2Lambda
            return Mono.defer(new Supplier<>() {
                @Override
                @SneakyThrows
                public Mono<? extends Resource> get() {
                    var document = readFullyAsString(resource.getInputStream());

                    if (title != null) {
                        document = ResourceUtilities.replaceTitle(document, title);
                    }

                    if (largeIconPath != null) {
                        document = ResourceUtilities.replaceLargeIcon(document, largeIconPath);
                    }

                    if (smallIconPath != null) {
                        document = ResourceUtilities.replaceSmallIcon(document, smallIconPath);
                    }

                    return Mono.just(new TransformedResource(resource, document.getBytes()));
                }
            });
        }

        return super.transform(serverWebExchange, resource, resourceTransformerChain);
    }
}