package com.project.soshuceapi.repositories;

import com.project.soshuceapi.entities.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, String>, PagingAndSortingRepository<Pet, String> {

    @Query(value = "SELECT nextval('pet_seq')", nativeQuery = true)
    Long getSEQ();

    @NonNull
    @Query(value = "SELECT * FROM pets " +
            "WHERE is_deleted = false " +
            "AND (:name = '' OR name LIKE %:name%) " +
            "AND (:breed = '' OR breed LIKE %:breed%) " +
            "AND (:color = '' OR color LIKE %:color%) " +
            "AND (:code = '' OR code LIKE %:code%) " +
            "AND (:type IS NULL OR type = :type) " +
            "AND (:age IS NULL OR age = :age) " +
            "AND (:status IS NULL OR status = :status) "
//            "ORDER BY status DESC " +
            , nativeQuery = true)
    Page<Pet> findAll(
            @Param("name") String name,
            @Param("breed") String breed,
            @Param("color") String color,
            @Param("code") String code,
            @Param("type") Integer type,
            @Param("age") Integer age,
            @Param("status") Integer status,
            Pageable pageable
    );



}
