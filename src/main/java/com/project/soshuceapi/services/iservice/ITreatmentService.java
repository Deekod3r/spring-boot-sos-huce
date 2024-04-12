package com.project.soshuceapi.services.iservice;

import com.project.soshuceapi.models.DTOs.TotalAmountStatisticDTO;
import com.project.soshuceapi.models.DTOs.TreatmentDTO;
import com.project.soshuceapi.models.requests.TotalTreatmentCostSearchRequest;
import com.project.soshuceapi.models.requests.TreatmentCreateRequest;
import com.project.soshuceapi.models.requests.TreatmentSearchRequest;
import com.project.soshuceapi.models.requests.TreatmentUpdateRequest;

import java.util.List;
import java.util.Map;

public interface ITreatmentService {

    Map<String, Object> getAll(TreatmentSearchRequest request);

    TreatmentDTO getById(String id);

    List<TotalAmountStatisticDTO> getTotalTreatmentCost(TotalTreatmentCostSearchRequest request);

    void create(TreatmentCreateRequest request);

    void update(TreatmentUpdateRequest request);

    void delete(String id, String deletedBy);
}
