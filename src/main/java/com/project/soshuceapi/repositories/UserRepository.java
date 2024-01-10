package com.project.soshuceapi.repositories;

import com.project.soshuceapi.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    @Query(value = "SELECT * FROM users WHERE email = :email and is_deleted = false", nativeQuery = true)
    Optional<User> findByEmail(String email);

    @Query(value = "SELECT COUNT(*) FROM users WHERE (phone_number = :phoneNumber OR email = :email) and is_deleted = false", nativeQuery = true)
    Long countByPhoneNumberOrEmail(@Param("phoneNumber") String phoneNumber, @Param("email") String email);

}
