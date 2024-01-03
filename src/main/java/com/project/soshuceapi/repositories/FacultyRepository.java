package com.project.soshuceapi.repositories;

import com.project.soshuceapi.entities.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, String> {

    @NonNull
    Optional<Faculty> findById(@NonNull String id);

}