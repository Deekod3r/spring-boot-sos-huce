package com.project.soshuceapi.repositories;

import com.project.soshuceapi.entities.NewsCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsCategoryRepo extends JpaRepository<NewsCategory, String> {

    @Query("SELECT nc FROM NewsCategory nc WHERE lower(nc.name) = lower(:name)")
    NewsCategory findByName(@Param("name") String name);

}