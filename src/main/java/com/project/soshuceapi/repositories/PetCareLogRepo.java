package com.project.soshuceapi.repositories;

import com.project.soshuceapi.entities.PetCareLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetCareLogRepo extends JpaRepository<PetCareLog, String> {
}
