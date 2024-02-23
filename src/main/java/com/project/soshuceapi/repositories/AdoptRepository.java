package com.project.soshuceapi.repositories;

import com.project.soshuceapi.entities.Adopt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdoptRepository extends JpaRepository<Adopt, String> {

    @Query(value = "SELECT nextval('adopt_seq')", nativeQuery = true)
    Long getSEQ();

}
