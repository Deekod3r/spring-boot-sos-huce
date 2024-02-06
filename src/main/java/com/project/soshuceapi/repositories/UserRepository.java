package com.project.soshuceapi.repositories;

import com.project.soshuceapi.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    @Query(value = "SELECT * FROM users WHERE lower(email) = lower(:email)", nativeQuery = true)
    Optional<User> findByEmail(String email);

    @Query(value = "SELECT COUNT(id) FROM users WHERE phone_number = :phoneNumber OR lower(email) = lower(:email)", nativeQuery = true)
    Long countByPhoneNumberOrEmail(@Param("phoneNumber") String phoneNumber, @Param("email") String email);

    @Query(value = "SELECT * FROM users WHERE phone_number = :phoneNumber OR lower(email) = lower(:email)", nativeQuery = true)
    Optional<User> findByPhoneNumberOrEmail(@Param("phoneNumber") String phoneNumber, @Param("email") String email);

}
