package com.project.soshuceapi.repositories;

import com.project.soshuceapi.entities.config.ConfigValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfigValueRepo extends JpaRepository<ConfigValue, Long>{

    @Query("SELECT cv FROM ConfigValue cv INNER JOIN Config c ON cv.configId = c.id WHERE c.key = :keyConfig")
    List<ConfigValue> findAllByConfig(@Param("keyConfig") String keyConfig);

}
