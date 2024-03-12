package com.project.soshuceapi.repositories;

import com.project.soshuceapi.entities.locations.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProvinceRepo extends JpaRepository<Province, Integer> {
}
