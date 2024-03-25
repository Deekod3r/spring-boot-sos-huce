package com.project.soshuceapi.repositories;

import com.project.soshuceapi.entities.config.Galleria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GalleriaRepo extends JpaRepository<Galleria, String> {

    @Query("SELECT g FROM Galleria g WHERE :status IS NULL OR g.status = :status ORDER BY g.index ASC")
    List<Galleria> findAll(@Param("status") Boolean status);

    @Query("select count(g) from Galleria g where g.status = ?1")
    long countByStatus(Boolean status);

}
