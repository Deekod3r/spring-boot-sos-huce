package com.project.soshuceapi.repositories;

import com.project.soshuceapi.entities.logging.ActionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionLogRepository extends JpaRepository<ActionLog, Long> {
}
