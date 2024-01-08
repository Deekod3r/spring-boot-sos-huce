package com.project.soshuceapi.repositories;

import com.project.soshuceapi.entities.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WardRepository extends JpaRepository<Ward, Integer> {
    @Query(value = "SELECT * FROM wards where district_id = :id", nativeQuery = true)
    List<Ward> findAllByDistrict(@Param("id") int id);
}
