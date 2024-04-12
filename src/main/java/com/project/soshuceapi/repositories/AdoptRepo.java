package com.project.soshuceapi.repositories;

import com.project.soshuceapi.entities.Adopt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface AdoptRepo extends JpaRepository<Adopt, String> {

    @NonNull
    @Query("SELECT a FROM Adopt a " +
            "JOIN FETCH a.pet " +
            "JOIN FETCH a.createdBy " +
            "JOIN FETCH a.registeredBy " +
            "LEFT JOIN FETCH a.confirmedBy " +
            "LEFT JOIN FETCH a.rejectedBy " +
            "WHERE a.isDeleted = false AND a.pet.isDeleted = false " +
            "AND (:status IS NULL OR a.status = :status) " +
            "AND (:code = '' OR a.code ILIKE CONCAT('%', :code, '%')) " +
            "AND (cast(:fromDate as timestamp) IS NULL OR a.createdAt >= :fromDate ) " +
            "AND (cast(:toDate as timestamp) IS NULL OR a.createdAt <= :toDate ) " +
            "AND (:registeredBy = '' OR a.registeredBy.id = :registeredBy) " +
            "AND (:petAdopt = '' OR a.pet.id = :petAdopt) " +
            "ORDER BY a.createdAt DESC, a.updatedAt DESC")
    Page<Adopt> findAll(
            @Param("status") Integer status,
            @Param("code") String code,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("registeredBy") String registeredBy,
            @Param("petAdopt") String petAdopt,
            Pageable pageable
    );

    @Query("SELECT a FROM Adopt a " +
            "JOIN FETCH a.pet " +
            "JOIN FETCH a.createdBy " +
            "JOIN FETCH a.registeredBy " +
            "LEFT JOIN FETCH a.confirmedBy " +
            "LEFT JOIN FETCH a.rejectedBy " +
            "WHERE a.isDeleted = false AND a.pet.isDeleted = false " +
            "AND a.registeredBy.id = :userId " +
            "ORDER BY a.status, a.updatedAt DESC, a.createdAt DESC")
    List<Adopt> findAllByUser(@NonNull @Param("userId") String userId);

    @NonNull
    @Query(value = "SELECT a FROM Adopt a WHERE a.isDeleted = false AND a.pet.isDeleted = false AND a.id = :id")
    Optional<Adopt> findById(@NonNull @Param("id") String id);

    @Query(value = "SELECT COUNT(1) FROM Adopt a " +
            "WHERE (:status IS NULL OR a.status IN :status) " +
            "AND a.isDeleted = false " +
            "AND a.pet.isDeleted = false " +
            "AND (:userId = '' OR a.registeredBy.id = :userId) ")
    long countByStatus(List<Integer> status, String userId);

    @Query(value = "SELECT " +
            "COUNT(CASE WHEN a.status = 1 THEN 1 END) AS countWaiting, " +
            "COUNT(CASE WHEN a.status = 2 THEN 1 END) AS countInProgress, " +
            "COUNT(CASE WHEN a.status = 3 THEN 1 END) AS countReject, " +
            "COUNT(CASE WHEN a.status = 4 THEN 1 END) AS countCancel, " +
            "COUNT(CASE WHEN a.status = 5 THEN 1 END) AS countComplete, " +
            "COUNT(1) AS total " +
            "FROM Adopt a " +
            "WHERE (:userId = '' OR a.registeredBy.id = :userId) " +
            "AND a.isDeleted = false " +
            "AND a.pet.isDeleted = false")
    Map<String, Long> countAll(String userId);

    @Query(value = "SELECT COUNT(1) FROM adopts " +
            "WHERE pet_id = :petId " +
            "AND registered_by = :userId " +
            "AND status IN (1, 2) " +
            "AND is_deleted = false", nativeQuery = true)
    long checkDuplicate(@Param("petId") String petId, @Param("userId") String userId);

    @Modifying
    @Query(value = "UPDATE adopts " +
            "SET " +
            "    status = 3, " +
            "    updated_at = CURRENT_TIMESTAMP, " +
            "    updated_by = :rejectBy, " +
            "    rejected_by = :rejectBy, " +
            "    rejected_at = CURRENT_TIMESTAMP, " +
            "    rejected_reason = 'Thú cưng đã được nhận nuôi' " +
            "WHERE " +
            "    status IN (1, 2) " +
            "    AND pet_id = :petId " +
            "    AND is_deleted = false", nativeQuery = true)
    void rejectAllByPet(@Param("petId") String petId, @Param("rejectBy") String rejectBy);


    @Query(value = "SELECT nextval('adopt_seq')", nativeQuery = true)
    long getSEQ();

    @Query("SELECT " +
            "    EXTRACT(YEAR FROM a.confirmedAt) AS year, " +
            "    EXTRACT(MONTH FROM a.confirmedAt) AS month, " +
            "    SUM(a.fee) AS total_amount " +
            "FROM " +
            "    Adopt a " +
            "WHERE " +
            "    a.isDeleted = FALSE " +
            "    AND a.status = 5 " +
            "    AND (:year IS NULL OR EXTRACT(YEAR FROM a.confirmedAt) = :year)" +
            "GROUP BY " +
            "    year, " +
            "    month " +
            "ORDER BY " +
            "    EXTRACT(YEAR FROM a.confirmedAt), " +
            "    EXTRACT(MONTH FROM a.confirmedAt) ")
    List<Object[]> calTotalFeeAdopt(
            @Param("year") Integer year
    );

    @Query(value = "SELECT adopts.id, adopts.code, " +
            "           adopts.confirmed_at + " +
            "           (SELECT CAST((config_values.value || ' days') AS INTERVAL) " +
            "           FROM config_values " +
            "           WHERE config_values.key_cv = 'CIRCLE_LOG') as check_date_first, " +
            "           adopts.confirmed_at + " +
            "           (SELECT CAST((CAST(config_values.value AS INTEGER) * 2 || ' days') AS INTERVAL) " +
            "           FROM config_values " +
            "           WHERE config_values.key_cv = 'CIRCLE_LOG') as check_date_second, " +
            "           adopts.confirmed_at + " +
            "           (SELECT CAST((CAST(config_values.value AS INTEGER) * 3 || ' days') AS INTERVAL) " +
            "           FROM config_values " +
            "           WHERE config_values.key_cv = 'CIRCLE_LOG') as check_date_third " +
            "FROM adopts " +
            "WHERE status = 5 " +
            "AND is_deleted = false " +
            "AND (" +
            "   (adopts.confirmed_at + " +
            "        (SELECT CAST((config_values.value || ' days') AS INTERVAL) " +
            "         FROM config_values " +
            "         WHERE config_values.key_cv = 'CIRCLE_LOG') - CURRENT_TIMESTAMP) " +
            "    BETWEEN CAST('0 days' AS INTERVAL) AND " +
            "            (SELECT CAST((config_values.value || ' days') AS INTERVAL) " +
            "             FROM config_values " +
            "             WHERE config_values.key_cv = 'DEAD_NIGHT')" +
            "   OR " +
            "   (adopts.confirmed_at + " +
            "        (SELECT CAST((CAST(config_values.value AS INTEGER) * 2 || ' days') AS INTERVAL) " +
            "         FROM config_values " +
            "         WHERE config_values.key_cv = 'CIRCLE_LOG') - CURRENT_TIMESTAMP) " +
            "    BETWEEN CAST('0 days' AS INTERVAL) AND " +
            "            (SELECT CAST((config_values.value || ' days') AS INTERVAL) " +
            "             FROM config_values " +
            "             WHERE config_values.key_cv = 'DEAD_NIGHT')" +
            "   OR " +
            "   (adopts.confirmed_at + " +
            "        (SELECT CAST((CAST(config_values.value AS INTEGER) * 3 || ' days') AS INTERVAL) " +
            "         FROM config_values " +
            "         WHERE config_values.key_cv = 'CIRCLE_LOG') - CURRENT_TIMESTAMP) " +
            "    BETWEEN CAST('0 days' AS INTERVAL) AND " +
            "            (SELECT CAST((config_values.value || ' days') AS INTERVAL) " +
            "             FROM config_values " +
            "             WHERE config_values.key_cv = 'DEAD_NIGHT')" +
            ")", nativeQuery = true)
    List<Object[]> findAdoptsByCircleLog();


}
