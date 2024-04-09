package com.project.soshuceapi.repositories;

import com.project.soshuceapi.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepo extends JpaRepository<Image, String> {

    @Query("SELECT i FROM Image i WHERE i.objectId = :objectId")
    List<Image> findByObjectId(@Param("objectId") String objectId);

}