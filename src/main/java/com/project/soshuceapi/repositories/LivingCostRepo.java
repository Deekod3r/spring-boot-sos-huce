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
import java.util.List;
import java.util.Optional;

@Repository
public interface LivingCostRepo extends JpaRepository<LivingCost, String> {

    @Query("SELECT lv FROM LivingCost lv " +
            "WHERE (cast(:fromDate as date) IS NULL OR lv.date >= :fromDate) " +
            "AND (cast(:toDate as date) IS NULL OR lv.date <= :toDate) " +
            "AND (:category IS NULL OR lv.category = :category) " +
            "AND lv.isDeleted = FALSE " +
            "ORDER BY lv.date DESC"
    )
    Page<LivingCost> findAll(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("category") Integer category,
            Pageable pageable
    );

    @NonNull
    @Query("SELECT lv FROM LivingCost lv " +
            "WHERE lv.id = :id " +
            "AND lv.isDeleted = false")
    Optional<LivingCost> findById(@NonNull @Param("id") String id);

    @Query("SELECT " +
            "    EXTRACT(YEAR FROM lv.date) AS year, " +
            "    EXTRACT(MONTH FROM lv.date) AS month, " +
            "    SUM(lv.cost) AS total_amount " +
            "FROM " +
            "    LivingCost lv " +
            "WHERE " +
            "    lv.isDeleted = FALSE " +
            "    AND EXTRACT(YEAR FROM lv.date) = :year " +
            "GROUP BY " +
            "    year, " +
            "    month " +
            "ORDER BY " +
            "    EXTRACT(YEAR FROM lv.date), " +
            "    EXTRACT(MONTH FROM lv.date) ")
    List<Object[]> calTotalLivingCost(
            @Param("year") Integer year
    );

    @Query("SELECT " +
            "    EXTRACT(YEAR FROM lv.date) AS year, " +
            "    EXTRACT(MONTH FROM lv.date) AS month, " +
            "    SUM(lv.cost) AS total_amount, " +
            "    lv.category AS category " +
            "FROM " +
            "    LivingCost lv " +
            "WHERE " +
            "   lv.isDeleted = FALSE " +
            "   AND EXTRACT(MONTH FROM lv.date) = :month " +
            "   AND EXTRACT(YEAR FROM lv.date) = :year " +
            "GROUP BY " +
            "    year, " +
            "    month," +
            "    category " +
            "ORDER BY " +
            "    EXTRACT(YEAR FROM lv.date), " +
            "    EXTRACT(MONTH FROM lv.date), " +
            "    category "
    )
    List<Object[]> calTotalLivingCostByCategoryAndMonth(
            @Param("year") Integer year,
            @Param("month") Integer month
    );

    @Query("SELECT " +
            "    EXTRACT(YEAR FROM lv.date) AS year, " +
            "    SUM(lv.cost) AS total_amount, " +
            "    lv.category AS category " +
            "FROM " +
            "    LivingCost lv " +
            "WHERE " +
            "   lv.isDeleted = FALSE " +
            "   AND EXTRACT(YEAR FROM lv.date) = :year " +
            "GROUP BY " +
            "    year, " +
            "    lv.category " +
            "ORDER BY lv.category "
    )
    List<Object[]> calTotalLivingCostByCategory(
            @Param("year") Integer year
    );


}