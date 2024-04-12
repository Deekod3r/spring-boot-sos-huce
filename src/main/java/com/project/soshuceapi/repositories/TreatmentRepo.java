package com.project.soshuceapi.repositories;

import com.project.soshuceapi.entities.Treatment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TreatmentRepo extends JpaRepository<Treatment, String> {


    @Query("SELECT t FROM Treatment t WHERE " +
            "(:petId = '' OR t.pet.id = :petId) " +
            "AND (:status IS NULL OR t.status = :status) " +
            "AND t.pet.isDeleted = FALSE AND t.isDeleted = FALSE ORDER BY t.endDate DESC")
    Page<Treatment> findAll(
            @Param("petId") String petId,
            @Param("status") Boolean status,
            Pageable pageable);

    @Query("SELECT " +
            "    EXTRACT(YEAR FROM t.endDate) AS year, " +
            "    EXTRACT(MONTH FROM t.endDate) AS month, " +
            "    SUM(t.price * t.quantity) AS total_amount " +
            "FROM " +
            "    Treatment t " +
            "WHERE " +
            "    t.isDeleted = FALSE " +
            "    AND t.pet.isDeleted = FALSE " +
            "    AND (:year IS NULL OR EXTRACT(YEAR FROM t.endDate) = :year)" +
            "GROUP BY " +
            "    year, " +
            "    month " +
            "ORDER BY " +
            "    EXTRACT(YEAR FROM t.endDate), " +
            "    EXTRACT(MONTH FROM t.endDate) ")
    List<Object[]> calTotalTreatmentCost(
            @Param("year") Integer year
    );

}