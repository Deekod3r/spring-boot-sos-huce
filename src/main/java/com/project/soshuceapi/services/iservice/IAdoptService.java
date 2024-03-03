package com.project.soshuceapi.services.iservice;

import com.project.soshuceapi.models.DTOs.AdoptDTO;
import com.project.soshuceapi.models.requests.AdoptCreateRequest;
import com.project.soshuceapi.models.requests.AdoptUpdateRequest;

import java.util.List;
import java.util.Map;

public interface IAdoptService {

    List<AdoptDTO> getAll();

    List<AdoptDTO> getAllByUser(String userId);

    Map<String, Object> getById(String id);

    AdoptDTO create(AdoptCreateRequest request);

    AdoptDTO update(AdoptUpdateRequest request);

    Boolean cancel(String id, String userId);

    Boolean deleteSoft(String id);

    Map<String, Long> statisticStatus(String userId);
}
