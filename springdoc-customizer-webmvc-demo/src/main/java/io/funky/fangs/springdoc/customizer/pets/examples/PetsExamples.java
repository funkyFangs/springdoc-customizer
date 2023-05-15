package io.funky.fangs.springdoc.customizer.pets.examples;

import io.funky.fangs.springdoc.customizer.annotation.ExampleDetails;
import io.funky.fangs.springdoc.customizer.annotation.ExampleMethod;
import io.funky.fangs.springdoc.customizer.annotation.ExampleTarget;
import io.funky.fangs.springdoc.customizer.annotation.ExampleType;
import io.funky.fangs.springdoc.customizer.pets.controller.PetsController;
import io.funky.fangs.springdoc.customizer.pets.model.Pet;
import io.funky.fangs.springdoc.customizer.pets.model.Species;

import java.time.LocalDate;
import java.time.Month;

import static io.funky.fangs.springdoc.customizer.annotation.ExampleType.Type.REQUEST;
import static io.funky.fangs.springdoc.customizer.annotation.ExampleType.Type.RESPONSE;
import static org.springframework.http.HttpStatus.OK;

@SuppressWarnings("unused") // Examples are targeted reflectively
public class PetsExamples {
    @ExampleDetails(name = "Giovanni",
            summary = "An Example of Giovanni",
            description = "This is an example of my smelly cat, Giovanni.",
            targets = @ExampleTarget(controller = PetsController.class, methods = {
                    @ExampleMethod(name = "get", types = @ExampleType(value = RESPONSE, responses = OK)),
                    @ExampleMethod(name = "create", types = @ExampleType(REQUEST)),
                    @ExampleMethod(name = "update", types = @ExampleType(REQUEST))
            }))
    private static final Pet PET_EXAMPLE = Pet.builder()
            .name("Giovanni")
            .species(Species.CAT)
            .birthday(LocalDate.of(2017, Month.APRIL, 21))
            .adoptable(false)
            .condition("Asthma")
            .build();
}