package com.project.soshuceapi.repositories;

import com.project.soshuceapi.entities.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface PetRepository extends JpaRepository<Pet, String> {

    @Query(value = "SELECT nextval('pet_seq')", nativeQuery = true)
    Long getSEQ();

    @NonNull
    @Query(value = "SELECT * FROM pets " +
            "WHERE is_deleted = false " +
            "AND (:name = '' OR name ILIKE %:name%) " +
            "AND (:breed = '' OR breed ILIKE %:breed%) " +
            "AND (:color = '' OR color ILIKE %:color%) " +
            "AND (:code = '' OR code ILIKE %:code%) " +
            "AND (:type IS NULL OR type = :type) " +
            "AND (:gender IS NULL OR gender = :gender) " +
            "AND (:age IS NULL OR age = :age) " +
            "AND (:status IS NULL OR status = :status) " +
            "ORDER BY created_at DESC "
            , nativeQuery = true)
    Page<Pet> findAll(
            @Param("name") String name,
            @Param("breed") String breed,
            @Param("color") String color,
            @Param("code") String code,
            @Param("type") Integer type,
            @Param("age") Integer age,
            @Param("gender") Integer gender,
            @Param("status") Integer status,
            Pageable pageable
    );

    Long countByStatus(int status);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE pets SET is_deleted = true WHERE id = :id", nativeQuery = true)
    int deleteSoftById(@NonNull @Param("id") String id);

}
