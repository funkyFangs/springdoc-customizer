package io.funky.fangs.springdoc.customizer.pets.repository;

import io.funky.fangs.springdoc.customizer.pets.model.Pet;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PetsRepository {
    private final Map<Long, Pet> pets;
    private final AtomicLong nextIdentifier;

    public PetsRepository() {
        pets = new ConcurrentHashMap<>();
        nextIdentifier = new AtomicLong();
    }

    public Pet get(long identifier) {
        return pets.get(identifier);
    }

    public long create(Pet pet) {
        var identifier = nextIdentifier.getAndIncrement();
        pet.setIdentifier(identifier);
        pets.put(identifier, pet);
        return identifier;
    }

    public boolean update(Pet pet) {
        return pets.put(pet.getIdentifier(), pet) != null;
    }

    public boolean delete(long identifier) {
        return pets.remove(identifier) != null;
    }
}