package com.project.soshuceapi.repositories;

import com.project.soshuceapi.entities.config.Config;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigRepo extends JpaRepository<Config, Long> {

    @Query("SELECT c FROM Config c WHERE c.key = :key")
    Config findByKey(@Param("key") String key);

}
