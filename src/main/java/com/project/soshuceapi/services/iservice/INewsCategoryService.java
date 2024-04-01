package com.project.soshuceapi.services.iservice;

import com.project.soshuceapi.models.DTOs.NewsCategoryDTO;
import com.project.soshuceapi.models.requests.NewsCategoryCreateRequest;
import com.project.soshuceapi.models.requests.NewsCategoryUpdateRequest;

import java.util.List;

public interface INewsCategoryService {
    List<NewsCategoryDTO> getAll();

    NewsCategoryDTO getById(String id);

    void create(NewsCategoryCreateRequest request);

    void update(NewsCategoryUpdateRequest request);

    void delete(String id, String deletedBy);
}
