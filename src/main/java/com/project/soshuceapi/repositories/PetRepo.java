package com.project.soshuceapi.repositories;

import com.project.soshuceapi.entities.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PetRepo extends JpaRepository<Pet, String> {

    @Query(value = "SELECT nextval('pet_seq')", nativeQuery = true)
    long getSEQ();

    @NonNull
    @Query("SELECT p FROM Pet p " +
            "LEFT JOIN FETCH p.adoptedBy " +
            "WHERE p.isDeleted = false " +
            "AND (:name = '' OR p.name ILIKE CONCAT('%', :name, '%')) " +
            "AND (:breed = '' OR p.breed ILIKE CONCAT('%', :breed, '%')) " +
            "AND (:color = '' OR p.color ILIKE CONCAT('%', :color, '%')) " +
            "AND (:code = '' OR p.code ILIKE CONCAT('%', :code, '%')) " +
            "AND (:type IS NULL OR p.type = :type) " +
            "AND (:gender IS NULL OR p.gender = :gender) " +
            "AND (:age IS NULL OR p.age = :age) " +
            "AND (:status IS NULL OR p.status = :status) " +
            "AND (:diet IS NULL OR p.diet = :diet) " +
            "AND (:vaccine IS NULL OR p.vaccine = :vaccine) " +
            "AND (:sterilization IS NULL OR p.sterilization = :sterilization) " +
            "AND (:rabies IS NULL OR p.rabies = :rabies) " +
            "AND (:adoptedBy = '' OR p.adoptedBy.id = :adoptedBy) " +
            "ORDER BY p.updatedAt DESC, p.createdAt DESC ")
    Page<Pet> findAll(
            @Param("name") String name,
            @Param("breed") String breed,
            @Param("color") String color,
            @Param("code") String code,
            @Param("type") Integer type,
            @Param("age") Integer age,
            @Param("gender") Integer gender,
            @Param("status") Integer status,
            @Param("diet") Integer diet,
            @Param("vaccine") Integer vaccine,
            @Param("sterilization") Integer sterilization,
            @Param("rabies") Integer rabies,
            @Param("adoptedBy") String adoptedBy,
            Pageable pageable
    );

    @NonNull
    @Query(value = "SELECT * FROM pets WHERE is_deleted = false AND id = :id", nativeQuery = true)
    Optional<Pet> findById(@NonNull @Param("id") String id);

    @Query(value = "SELECT count(1) FROM pets WHERE is_deleted = false", nativeQuery = true)
    long count();

    @Query(value = "SELECT count(1) FROM pets WHERE is_deleted = false AND status = :status", nativeQuery = true)
    long countByStatus(int status);

}
