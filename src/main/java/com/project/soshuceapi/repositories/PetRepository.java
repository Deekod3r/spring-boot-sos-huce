package com.project.soshuceapi.repositories;

import com.project.soshuceapi.entities.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, String> {

    @Query(value = "SELECT nextval('pet_seq')", nativeQuery = true)
    Long getSEQ();

    @NonNull
    @Query(value = "SELECT * FROM pets WHERE is_deleted = false ORDER BY status DESC", nativeQuery = true)
    List<Pet> findAll();

}
