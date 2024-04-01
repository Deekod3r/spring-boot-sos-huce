package com.project.soshuceapi.repositories;

import com.project.soshuceapi.entities.FeedbackBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackBoxRepo extends JpaRepository<FeedbackBox, String> {
}