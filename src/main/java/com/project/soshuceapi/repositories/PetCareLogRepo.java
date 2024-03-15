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

    @Query("SELECT p FROM PetCareLog p JOIN FETCH p.adopt " +
            "WHERE p.adopt.id = :adoptId " +
            "AND p.adopt.isDeleted = false " +
            "AND (:fromDate IS NULL OR p.date >= :fromDate) " +
            "AND (:toDate IS NULL OR p.date <= :toDate) " +
            "ORDER BY p.date DESC, p.updatedAt DESC, p.createdAt DESC")
    List<PetCareLog> findAll(
            @Param("adoptId") String adoptId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );


}
