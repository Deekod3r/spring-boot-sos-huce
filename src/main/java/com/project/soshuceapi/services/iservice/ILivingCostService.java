package com.project.soshuceapi.services.iservice;

import com.project.soshuceapi.models.DTOs.LivingCostDTO;
import com.project.soshuceapi.models.DTOs.TotalAmountStatisticDTO;
import com.project.soshuceapi.models.requests.LivingCostCreateRequest;
import com.project.soshuceapi.models.requests.LivingCostSearchRequest;
import com.project.soshuceapi.models.requests.LivingCostUpdateRequest;
import com.project.soshuceapi.models.requests.TotalLivingCostSearchRequest;

import java.util.List;
import java.util.Map;

public interface ILivingCostService {

    Map<String, Object> getAll(LivingCostSearchRequest request);

    LivingCostDTO getById(String id);

    List<TotalAmountStatisticDTO> getTotalLivingCost(TotalLivingCostSearchRequest request);

    List<TotalAmountStatisticDTO> getTotalLivingCostByCategory(TotalLivingCostSearchRequest request);

    void create(LivingCostCreateRequest request);

    void update(LivingCostUpdateRequest request);

    void delete(String id, String deletedBy);

}
