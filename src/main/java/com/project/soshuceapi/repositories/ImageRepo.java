package com.project.soshuceapi.repositories;

import com.project.soshuceapi.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepo extends JpaRepository<Image, String> {
}