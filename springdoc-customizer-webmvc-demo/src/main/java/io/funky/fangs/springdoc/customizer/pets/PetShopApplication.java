package io.funky.fangs.springdoc.customizer.pets;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(title = "Pet Shop",
        description = "A REST service for managing pets."))
@SpringBootApplication
public class PetShopApplication {
    public static void main(String[] arguments) {
        SpringApplication.run(PetShopApplication.class, arguments);
    }
}