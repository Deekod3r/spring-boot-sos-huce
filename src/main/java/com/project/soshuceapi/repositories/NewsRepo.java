package com.project.soshuceapi.repositories;

import com.project.soshuceapi.entities.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface NewsRepo extends JpaRepository<News, String> {

    @Query("SELECT n.id, n.title, n.description, n.image, n.status, n.newsCategory.id, n.newsCategory.name ,n.createdAt FROM News n " +
            "WHERE (:title = '' OR n.title ILIKE CONCAT('%', :title, '%')) " +
            "AND (:categoryId = '' OR n.newsCategory.id = :categoryId) " +
            "AND (:status IS NULL OR n.status = :status) " +
            "AND (cast(:fromDate AS timestamp) IS NULL OR n.createdAt >= :fromDate ) " +
            "AND (cast(:toDate AS timestamp) IS NULL OR n.createdAt <= :toDate )" +
            "ORDER BY n.createdAt DESC"
    )
    Page<Object[]> findAll(
            @Param("title") String title,
            @Param("categoryId") String categoryId,
            @Param("status") Boolean status,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);

    @Query("SELECT n FROM News n WHERE lower(n.title) = lower(:title)")
    News findByTitle(@Param("title") String title);

    @Modifying
    @Query("DELETE FROM News n WHERE n.newsCategory.id = :newsCategoryId")
    void deleteByNewsCategory(@Param("newsCategoryId") String newsCategoryId);

}