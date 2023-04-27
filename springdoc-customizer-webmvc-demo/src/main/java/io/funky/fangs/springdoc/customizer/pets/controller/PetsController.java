package io.funky.fangs.springdoc.customizer.pets.controller;

import io.funky.fangs.springdoc.customizer.pets.model.Pet;
import io.funky.fangs.springdoc.customizer.pets.service.PetsService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pets")
@AllArgsConstructor
public class PetsController {
    private PetsService service;

    @GetMapping("/{identifier}")
    public Pet get(@PathVariable long identifier) {
        return service.get(identifier);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public long create(@RequestBody Pet pet) {
        return service.create(pet);
    }

    @PutMapping("/{identifier}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable long identifier, @RequestBody Pet pet) {
        service.update(identifier, pet);
    }

    @DeleteMapping("/{identifier}")
    public void delete(@PathVariable long identifier) {
        service.delete(identifier);
    }
}