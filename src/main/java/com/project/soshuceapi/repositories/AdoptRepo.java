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

    @Query("SELECT a.id, a.code, a.wardId, a.districtId, a.provinceId, a.fee, a.address, a.status, a.reason, a.confirmedAt, " +
            "a.rejectedAt, a.rejectedReason, a.createdAt, a.pet.id AS petId, CONCAT(a.pet.name, ' - ', a.pet.code) AS petName, " +
            "a.createdBy.id AS createdBy, CONCAT(a.createdBy.role, ' - ', a.createdBy.name) AS nameCreatedBy, " +
            "a.registeredBy.id AS registeredBy, a.registeredBy.name AS nameRegisteredBy, a.registeredBy.email AS emailRegisteredBy, " +
            "a.registeredBy.phoneNumber AS phoneRegisteredBy, a.confirmedBy.id AS confirmedBy, a.confirmedBy.name AS nameConfirmedBy, " +
            "a.rejectedBy.id AS rejectedBy, a.rejectedBy.name AS nameRejectedBy, " +
            "(SELECT w.name FROM Ward w WHERE w.id = a.wardId) AS wardName, " +
            "(SELECT d.name FROM District d WHERE d.id = a.districtId) AS districtName, " +
            "(SELECT p.name FROM Province p WHERE p.id = a.provinceId) AS provinceName " +
            "FROM Adopt a " +
            "LEFT JOIN a.confirmedBy " +
            "LEFT JOIN a.rejectedBy " +
            "WHERE a.isDeleted = FALSE AND a.pet.isDeleted = FALSE " +
            "AND (:status IS NULL OR a.status = :status) " +
            "AND (:code = '' OR a.code ILIKE CONCAT('%', :code, '%')) " +
            "AND (cast(:fromDate AS timestamp) IS NULL OR a.createdAt >= :fromDate ) " +
            "AND (cast(:toDate AS timestamp) IS NULL OR a.createdAt <= :toDate ) " +
            "AND (:registeredBy = '' OR a.registeredBy.id = :registeredBy) " +
            "AND (:petAdopt = '' OR a.pet.id = :petAdopt) " +
            "ORDER BY a.createdAt DESC, a.updatedAt DESC")
    Page<Object[]> findAll(
            @Param("status") Integer status,
            @Param("code") String code,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("registeredBy") String registeredBy,
            @Param("petAdopt") String petAdopt,
            Pageable pageable
    );

    @NonNull
    @Query(value = "SELECT a " +
            "FROM Adopt a " +
            "WHERE a.isDeleted = false AND a.pet.isDeleted = false AND a.id = :id")
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
            "    AND EXTRACT(YEAR FROM a.confirmedAt) = :year " +
            "GROUP BY " +
            "    year, " +
            "    month " +
            "ORDER BY " +
            "    EXTRACT(YEAR FROM a.confirmedAt), " +
            "    EXTRACT(MONTH FROM a.confirmedAt) ")
    List<Object[]> calTotalFeeAdopt(
            @Param("year") Integer year
    );

    @Query(value = "SELECT adopts.id, adopts.code, users.name, users.phone_number, users.email, " +
            "           adopts.confirmed_at + " +
            "           (SELECT CAST((config_values.value || ' days') AS INTERVAL) " +
            "           FROM config_values " +
            "           WHERE config_values.key_cv = 'CIRCLE_LOG') AS check_date_first, " +
            "           adopts.confirmed_at + " +
            "           (SELECT CAST((CAST(config_values.value AS INTEGER) * 2 || ' days') AS INTERVAL) " +
            "           FROM config_values " +
            "           WHERE config_values.key_cv = 'CIRCLE_LOG') AS check_date_second, " +
            "           adopts.confirmed_at + " +
            "           (SELECT CAST((CAST(config_values.value AS INTEGER) * 3 || ' days') AS INTERVAL) " +
            "           FROM config_values " +
            "           WHERE config_values.key_cv = 'CIRCLE_LOG') AS check_date_third " +
            "FROM adopts INNER JOIN users on users.id = adopts.registered_by " +
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
            ")  AND (SELECT COUNT(1) FROM pet_care_logs WHERE adopt_id = adopts.id) < 3 " +
            "   ORDER BY adopts.confirmed_at DESC", nativeQuery = true)
    List<Object[]> findAdoptsNearLog();


}
