package cl.veterinary.repository;


import cl.veterinary.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet,Long> {


}
