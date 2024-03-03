package com.project.soshuceapi.repositories;

import com.project.soshuceapi.entities.Adopt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdoptRepository extends JpaRepository<Adopt, String> {

    @Query(value = "SELECT nextval('adopt_seq')", nativeQuery = true)
    Long getSEQ();

    @Query(value = "SELECT * FROM adopts " +
            "WHERE created_by = :userId " +
            "AND is_deleted = false ORDER BY status, created_at DESC", nativeQuery = true)
    List<Adopt> findAllByUser(@NonNull @Param("userId") String userId);

    @NonNull
    @Query(value = "SELECT * FROM adopts WHERE is_deleted = false AND id = :id", nativeQuery = true)
    Optional<Adopt> findById(@NonNull @Param("id") String id);

    @Query(value = "SELECT COUNT(1) FROM adopts " +
            "WHERE status = :status " +
            "AND (:userId = '' OR registered_by = :userId) ", nativeQuery = true)
    Long countByStatus(Integer status, String userId);

    @Query(value = "SELECT COUNT(1) FROM adopts " +
            "WHERE pet_id = :petId " +
            "AND registered_by = :userId AND status NOT IN (4, 6) ", nativeQuery = true)
    Long checkDuplicate(String petId, String userId);
}
