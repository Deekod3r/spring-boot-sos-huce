package com.project.soshuceapi.repositories;

import com.project.soshuceapi.entities.Adopt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
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
            "ORDER BY a.status, a.updatedAt DESC, a.createdAt DESC")
    Page<Adopt> findAll(
            @Param("status") Integer status,
            @Param("code") String code,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("registeredBy") String registeredBy,
            @Param("petAdopt") String petAdopt,
            Pageable pageable
    );

    @Query(value = "SELECT nextval('adopt_seq')", nativeQuery = true)
    long getSEQ();

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
    @Query(value = "SELECT * FROM adopts WHERE is_deleted = false AND id = :id", nativeQuery = true)
    Optional<Adopt> findById(@NonNull @Param("id") String id);

    @Query(value = "SELECT COUNT(1) FROM Adopt a " +
            "WHERE " +
            "(:status IS NULL OR a.status = :status) " +
            "AND a.isDeleted = false " +
            "AND a.pet.isDeleted = false " +
            "AND (:userId = '' OR a.registeredBy.id = :userId) ")
    long countByStatus(Integer status, String userId);

    @Query(value = "SELECT COUNT(1) FROM adopts " +
            "WHERE pet_id = :petId " +
            "AND registered_by = :userId AND status NOT IN (3, 4, 5) AND is_deleted = false", nativeQuery = true)
    long checkDuplicate(String petId, String userId);
}
