package com.project.soshuceapi.repositories;

import com.project.soshuceapi.entities.PetCareLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PetCareLogRepo extends JpaRepository<PetCareLog, String> {

    @Query("SELECT p FROM PetCareLog p " +
            "JOIN FETCH p.adopt " +
            "WHERE p.adopt.isDeleted = FALSE " +
            "AND (:adoptId = '' OR p.adopt.id = :adoptId) " +
            "AND (:petId = '' OR p.adopt.pet.id = :petId) " +
            "AND (cast(:fromDate as date) IS NULL OR p.date >= :fromDate) " +
            "AND (cast(:toDate as date) IS NULL OR p.date <= :toDate) " +
            "ORDER BY p.date DESC, p.createdAt DESC, p.updatedAt DESC")
    List<PetCareLog> findAll(
            @Param("adoptId") String adoptId,
            @Param("petId") String petId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

}
