package com.project.soshuceapi.repositories;

import com.project.soshuceapi.entities.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NewsRepo extends JpaRepository<News, String> {

    @Query("SELECT n FROM News n " +
            "WHERE (:title = '' OR n.title ILIKE CONCAT('%', :title, '%')) " +
            "AND (:categoryId = '' OR n.newsCategory.id = :categoryId) " +
            "AND (:status IS NULL OR n.status = :status) " +
            "AND (cast(:fromDate as timestamp) IS NULL OR n.createdAt >= :fromDate ) " +
            "AND (cast(:toDate as timestamp) IS NULL OR n.createdAt <= :toDate )")
    List<News> findAll(
            @Param("title") String title,
            @Param("categoryId") String categoryId,
            @Param("status") Boolean status,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    @Query("SELECT n FROM News n WHERE lower(n.title) = lower(:title)")
    News findByTitle(@Param("title") String title);

    @Modifying
    @Query("DELETE FROM News n WHERE n.newsCategory.id = :newsCategoryId")
    void deleteByNewsCategory(@Param("newsCategoryId") String newsCategoryId);

}