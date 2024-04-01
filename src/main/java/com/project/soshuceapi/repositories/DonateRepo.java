package com.project.soshuceapi.repositories;

import com.project.soshuceapi.entities.Donate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonateRepo extends JpaRepository<Donate, String> {
}