package com.project.soshuceapi.repositories;

import com.project.soshuceapi.entities.Donate;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DonateRepo extends JpaRepository<Donate, String> {

    @Query("SELECT d FROM Donate d " +
            "WHERE (:remitter = '' OR d.remitter ILIKE CONCAT('%', :remitter, '%')) " +
            "AND (:payee = '' OR d.payee ILIKE CONCAT('%', :payee, '%')) " +
            "AND (cast(:fromDate as date) IS NULL OR d.date >= :fromDate ) " +
            "AND (cast(:toDate as date) IS NULL OR d.date <= :toDate ) " +
            "AND d.isDeleted = FALSE " +
            "ORDER BY d.date DESC"
    )
    Page<Donate> findAll(
            @Param("remitter") String remitter,
            @Param("payee") String payee,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable);

    @NonNull
    @Query("SELECT d FROM Donate d " +
            "WHERE d.id = :id " +
            "AND d.isDeleted = FALSE"
    )
    Optional<Donate> findById(@NonNull @Param("id") String id);

    @Query("SELECT " +
            "    EXTRACT(YEAR FROM d.date) AS year, " +
            "    EXTRACT(MONTH FROM d.date) AS month, " +
            "    SUM(d.amount) AS total_amount " +
            "FROM " +
            "    Donate d " +
            "WHERE " +
            "    d.isDeleted = FALSE " +
            "    AND EXTRACT(YEAR FROM d.date) = :year " +
            "GROUP BY " +
            "    year, " +
            "    month " +
            "ORDER BY " +
            "    EXTRACT(YEAR FROM d.date), " +
            "    EXTRACT(MONTH FROM d.date) ")
    List<Object[]> calTotalDonation(
            @Param("year") Integer year
    );

}