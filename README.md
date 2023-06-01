# springdoc-customizer
A module for fine-grain customization of SpringDoc REST Services

## About
[springdoc-openapi](https://github.com/springdoc) provides the ability to generate OpenAPI specifications from Spring Boot REST services and offers
features for customization. The goal of this project is to extend these customization features to allow Spring Boot services to leverage helpful
features, such as serialized example injection or version customization.

This project supports Spring Boot's autoconfiguration and does not need much configuration out of the box.

## Features
### Examples
Examples in OpenAPI are an incredibly important feature, especially for complex endpoints. However, in Swagger's annotations, examples can only be stored as literal strings; this creates several problems with defining examples as a result.
Literal strings cannot enforce the accuracy of the JSON values inside, and writing raw JSON without explicit type support can be tedious. Additionally, as a class model changes, example strings can become out of date and require additional maintenance.

With springdoc-customizer, we can forego these issues by declaring constant fields annotated with `@ExampleDetails`. By doing this, you can ensure your examples are both aligned with your class model as well as valid based on the validation annotations (such as Jakarta's `@NonNull` annotation).

Here is an example of this annotation in use.

```java
@ExampleDetails(name = "My Example",
        summary = "An example of mine",
        description = "This is a custom example of mine. It demonstrates the usage of the @ExampleDetails annotation.",
        targets = @ExampleTarget(controller = MyController.class,
                methods = @ExampleMethod(name = "create", types = @ExampleType(REQUEST))))
public static final Resource RESOURCE = Resource.builder()
        .stringField("String")
        .intField(99)
        .build();
```

This tells springdoc-customizer to inject the value of this field into the OpenAPI specification under the request path for the `create` method in `MyController`; this will in turn serialize this example into the following JSON.

```json
{
  "stringField": "String",
  "intField": 99
}
```

Let's assume that `intField` is annotated with `@Size(min = 100)`; if `springdoc.customizer.examples.validate-examples` is set to `true`, then this example would be excluded from the OpenAPI specification because `intField` is invalid.

### Specification Version
Often times, it is helfpul to set the OpenAPI specification's version to the version of the Spring Boot project. By setting `springdoc.customizer.customize-version` to `true`, this will set the specification's version to the project's version.

### Swagger UI
The Swagger UI is an incredibly powerful feature for any platforms which serve an OpenAPI specification. It allows for an interactive visualization of a REST service and is highly configurable. However, the webpage itself lacks support for customization
without the use of raw JS which is undesireable for a Spring Boot project.

With springdoc-customizer, you can set the page's icon and title with a simple configuration. Simply set `springdoc.swagger-ui.title` and `springdoc.swagger-ui.large-icon-path`/`springdoc.swagger-ui.small-icon-path` values in your configuration to
customize your page. Note that `large-icon-path` and `small-icon-path` must point to valid 32x32 and 16x16 images respectively within the `resources/static` folder.

## Demo
A demo project, [springdoc-customizer-webmvc-demo](https://github.com/funkyFangs/springdoc-customizer/tree/develop/springdoc-customizer-webmvc-demo), has been included to demonstrate available features.
