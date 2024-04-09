package com.project.soshuceapi.services.iservice;

import com.project.soshuceapi.models.DTOs.LivingCostDTO;
import com.project.soshuceapi.models.requests.LivingCostCreateRequest;
import com.project.soshuceapi.models.requests.LivingCostSearchRequest;
import com.project.soshuceapi.models.requests.LivingCostUpdateRequest;

import java.util.Map;

public interface ILivingCostService {
    Map<String, Object> getAll(LivingCostSearchRequest request);

    LivingCostDTO getById(String id);

    void create(LivingCostCreateRequest request);

    void update(LivingCostUpdateRequest request);

    void delete(String id, String deletedBy);
}
