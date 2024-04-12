package com.project.soshuceapi.repositories;

import com.project.soshuceapi.entities.FeedbackBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FeedbackBoxRepo extends JpaRepository<FeedbackBox, String> {

    @Query(
        value = "SELECT * FROM feedback_boxes WHERE created_at BETWEEN :fromDate AND :toDate ORDER BY created_at DESC",
        nativeQuery = true
    )
    List<FeedbackBox> findAll(
        @Param("fromDate") LocalDate fromDate,
        @Param("toDate") LocalDate toDate
    );

}