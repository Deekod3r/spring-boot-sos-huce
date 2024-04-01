package com.project.soshuceapi.repositories;

import com.project.soshuceapi.entities.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankRepo extends JpaRepository<Bank, String> {

    @Query("SELECT COUNT(b) FROM Bank b WHERE b.isDeleted = false")
    long count();

    @NonNull
    @Query("SELECT b FROM Bank b WHERE b.isDeleted = false")
    List<Bank> findAll();

    @NonNull
    @Query("SELECT b FROM Bank b WHERE b.id = :id AND b.isDeleted = false")
    Optional<Bank> findById(@NonNull @Param("id") String id);

    @Query("SELECT b FROM Bank b WHERE b.accountNumber = :accountNumber AND b.isDeleted = false")
    Bank findByAccountNumber(@Param("accountNumber") String accountNumber);

}