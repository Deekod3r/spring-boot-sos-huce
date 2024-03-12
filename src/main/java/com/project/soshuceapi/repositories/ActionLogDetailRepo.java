package com.project.soshuceapi.repositories;

import com.project.soshuceapi.entities.logging.ActionLogDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionLogDetailRepo extends JpaRepository<ActionLogDetail, Long> {
}
