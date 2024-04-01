package com.project.soshuceapi.repositories;

import com.project.soshuceapi.entities.LivingCost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LivingCostRepo extends JpaRepository<LivingCost, String> {
}