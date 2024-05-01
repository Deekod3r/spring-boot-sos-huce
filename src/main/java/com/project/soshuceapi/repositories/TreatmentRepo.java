package com.project.soshuceapi.repositories;

import com.project.soshuceapi.entities.Treatment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;

@Repository
public interface TreatmentRepo extends JpaRepository<Treatment, String> {


    @Query("SELECT t FROM Treatment t WHERE " +
            "(:petId = '' OR t.pet.id = :petId) " +
            "AND (:status IS NULL OR t.status = :status) " +
            "AND (:type IS NULL OR t.type = :type) " +
            "AND (:daysOfTreatment IS NULL OR t.endDate - t.startDate < :daysOfTreatment) " +
            "AND t.pet.isDeleted = FALSE AND t.isDeleted = FALSE ORDER BY t.endDate DESC, t.createdAt DESC")
    Page<Treatment> findAll(
            @Param("petId") String petId,
            @Param("status") Boolean status,
            @Param("type") Integer type,
            @Param("daysOfTreatment") Duration daysOfTreatment,
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
            "    AND EXTRACT(YEAR FROM t.endDate) = :year " +
            "GROUP BY " +
            "    year, " +
            "    month " +
            "ORDER BY " +
            "    EXTRACT(YEAR FROM t.endDate), " +
            "    EXTRACT(MONTH FROM t.endDate) ")
    List<Object[]> calTotalTreatmentCost(
            @Param("year") Integer year
    );

    @Query("SELECT " +
            "    EXTRACT(YEAR FROM tr.endDate) AS year, " +
            "    EXTRACT(MONTH FROM tr.endDate) AS month, " +
            "    SUM(tr.price * tr.quantity) AS total_amount, " +
            "    tr.type AS type " +
            "FROM " +
            "    Treatment tr " +
            "WHERE " +
            "   tr.isDeleted = FALSE " +
            "   AND EXTRACT(MONTH FROM tr.endDate) = :month " +
            "   AND EXTRACT(YEAR FROM tr.endDate) = :year " +
            "GROUP BY " +
            "    year, " +
            "    month," +
            "    type " +
            "ORDER BY " +
            "    EXTRACT(YEAR FROM tr.endDate), " +
            "    EXTRACT(MONTH FROM tr.endDate), type ")
    List<Object[]> calTotalTreatmentCostByTypeAndMonth(
            @Param("year") Integer year,
            @Param("month") Integer month
    );

    @Query("SELECT " +
            "    EXTRACT(YEAR FROM tr.endDate) AS year, " +
            "    SUM(tr.price * tr.quantity) AS total_amount, " +
            "    tr.type AS type " +
            "FROM " +
            "    Treatment tr " +
            "WHERE " +
            "   tr.isDeleted = FALSE " +
            "   AND EXTRACT(YEAR FROM tr.endDate) = :year " +
            "GROUP BY " +
            "    year, " +
            "    type " +
            "ORDER BY type"
    )
    List<Object[]> calTotalTreatmentCostByType(
            @Param("year") Integer year
    );

}