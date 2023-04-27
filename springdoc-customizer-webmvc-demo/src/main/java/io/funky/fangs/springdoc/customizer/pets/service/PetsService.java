package io.funky.fangs.springdoc.customizer.pets.service;

import io.funky.fangs.springdoc.customizer.pets.exception.BadRequestException;
import io.funky.fangs.springdoc.customizer.pets.exception.NotFoundException;
import io.funky.fangs.springdoc.customizer.pets.model.Pet;
import io.funky.fangs.springdoc.customizer.pets.repository.PetsRepository;
import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class PetsService {
    private final PetsRepository repository;

    @Nonnull
    public Pet get(long identifier) {
        return Optional.ofNullable(repository.get(identifier))
                    .orElseThrow(() -> new NotFoundException("Could not find pet"));
    }

    public long create(Pet pet) {
        if (pet.getIdentifier() != null) {
            throw new BadRequestException("Pet identifier must not be populated in body");
        }

        return repository.create(pet);
    }

    public void update(long identifier, Pet pet) {
        if (pet.getIdentifier() != null) {
            throw new BadRequestException("Pet identifier must not be populated in body");
        }

        pet.setIdentifier(identifier);

        if (!repository.update(pet)) {
            throw new NotFoundException("Could not find Pet [Identifier: %s]".formatted(identifier));
        }
    }

    public void delete(long identifier) {
        if (!repository.delete(identifier)) {
            throw new NotFoundException("Could not find Pet [Identifier: %s]".formatted(identifier));
        }
    }
}