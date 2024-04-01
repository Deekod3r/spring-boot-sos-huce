package com.project.soshuceapi.repositories;

import com.project.soshuceapi.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, String> {

    @Query(value = "SELECT * FROM users WHERE lower(email) = lower(:email)", nativeQuery = true)
    Optional<User> findByEmail(@Param("email") String email);

    @Query(value = "SELECT * FROM users WHERE phone_number = :phoneNumber", nativeQuery = true)
    Optional<User> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    @Query(value = "SELECT COUNT(id) FROM users WHERE phone_number = :phoneNumber OR lower(email) = lower(:email)", nativeQuery = true)
    Long countByPhoneNumberOrEmail(@Param("phoneNumber") String phoneNumber, @Param("email") String email);

    @Query(value = "SELECT * FROM users WHERE phone_number = :phoneNumber OR lower(email) = lower(:email)", nativeQuery = true)
    Optional<User> findByPhoneNumberOrEmail(@Param("phoneNumber") String phoneNumber, @Param("email") String email);

    @Query(value = "SELECT * FROM users " +
            "WHERE (:name = '' OR name ILIKE CONCAT('%', :name, '%')) " +
            "AND (:email = '' OR email ILIKE CONCAT('%', :email, '%')) " +
            "AND (:phoneNumber = '' OR phone_number ILIKE CONCAT('%', :phoneNumber, '%')) " +
            "AND (:isActivated IS NULL OR is_activated = :isActivated) " +
            "AND (:role = '' OR role = :role) " +
            "ORDER BY created_at DESC "
            , nativeQuery = true)
    Page<User> getAll(
            @Param("name") String name,
            @Param("email") String email,
            @Param("phoneNumber") String phoneNumber,
            @Param("isActivated") Boolean isActivated,
            @Param("role") String role,
            Pageable pageable
    );

}
