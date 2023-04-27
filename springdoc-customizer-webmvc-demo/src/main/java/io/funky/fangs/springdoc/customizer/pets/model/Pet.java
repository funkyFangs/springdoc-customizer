package io.funky.fangs.springdoc.customizer.pets.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class Pet {
    @Schema(description = "The internal identifier for this pet")
    private Long identifier;

    @Schema(description = "The name of this pet")
    @Nonnull
    private String name;

    @Schema(description = "The date this pet was born")
    private LocalDate birthday;

    @Schema(description = "The species of this pet")
    @Nonnull
    private Species species;

    @Schema(description = "Whether this pet is currently available for adoption")
    private boolean adoptable;

    @Singular
    @Schema(description = "The list of conditions, if any, this pet has.")
    private List<String> conditions;
}