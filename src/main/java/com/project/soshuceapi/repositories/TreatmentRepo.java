package com.project.soshuceapi.repositories;

import com.project.soshuceapi.entities.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TreatmentRepo extends JpaRepository<Treatment, String> {
}