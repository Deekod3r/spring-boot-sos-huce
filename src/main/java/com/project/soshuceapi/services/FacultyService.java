package com.project.soshuceapi.services;

import com.project.soshuceapi.entities.Faculty;
import com.project.soshuceapi.repositories.FacultyRepository;
import com.project.soshuceapi.services.iservice.IFacultyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacultyService implements IFacultyService {

    @Autowired
    private FacultyRepository facultyRepository;

    @Override
    public List<Faculty> getAll() {
        return facultyRepository.findAll();
    }

    @Override
    public Faculty getById(String id) {
        return facultyRepository.findById(id).orElseThrow(() ->
                new RuntimeException("not.found.faculty"));
    }

    @Override
    public boolean isExistsById(String id) {
        return facultyRepository.existsById(id);
    }

}
