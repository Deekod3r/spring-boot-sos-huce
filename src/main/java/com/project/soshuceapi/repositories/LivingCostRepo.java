package com.project.soshuceapi.repositories;

import com.project.soshuceapi.entities.LivingCost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface LivingCostRepo extends JpaRepository<LivingCost, String> {

    @Query("SELECT lv FROM LivingCost lv " +
            "WHERE (cast(:fromDate as date) IS NULL OR lv.date >= :fromDate) " +
            "AND (cast(:toDate as date) IS NULL OR lv.date <= :toDate) " +
            "AND lv.isDeleted = false " +
            "ORDER BY lv.date DESC"
    )
    Page<LivingCost> findAll(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable
    );

    @NonNull
    @Query("SELECT lv FROM LivingCost lv " +
            "WHERE lv.id = :id " +
            "AND lv.isDeleted = false")
    Optional<LivingCost> findById(@NonNull @Param("id") String id);

}