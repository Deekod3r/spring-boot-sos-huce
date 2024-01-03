package com.project.soshuceapi.services.iservice;

import com.project.soshuceapi.entities.Faculty;

import java.util.List;

public interface IFacultyService {

    List<Faculty> getAll();

    Faculty getById(String id);

    boolean isExistsById(String id);

}
