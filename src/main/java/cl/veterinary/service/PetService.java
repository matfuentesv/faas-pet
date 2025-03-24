package cl.veterinary.service;

import cl.veterinary.model.Pet;

import java.util.List;
import java.util.Optional;

public interface PetService {

    List<Pet>findAll();
    Optional<Pet> findPetById(Long id);
    Pet savePet(Pet Pet);
    Pet updatePet(Pet Pet);
    void deletePet(Long id);
}
