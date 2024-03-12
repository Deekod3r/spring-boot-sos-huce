package com.project.soshuceapi.repositories;

import com.project.soshuceapi.entities.locations.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistrictRepo extends JpaRepository<District, Integer> {

    @Query(value = "SELECT * FROM districts where province_id = :id", nativeQuery = true)
    List<District> findAllByProvince(@Param("id") int id);

}
