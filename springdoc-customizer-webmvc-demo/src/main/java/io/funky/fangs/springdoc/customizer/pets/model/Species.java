package io.funky.fangs.springdoc.customizer.pets.model;

import io.swagger.v3.oas.annotations.media.Schema;

@SuppressWarnings("unused") // This is a demo
@Schema(description = "Represents a type of animal")
public enum Species {
    BIRD,
    CAT,
    DOG,
    FISH,
    FROG,
    LIZARD
}