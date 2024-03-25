package com.project.soshuceapi.services.iservice;

import com.project.soshuceapi.models.DTOs.AdoptDTO;
import com.project.soshuceapi.models.requests.AdoptCreateRequest;
import com.project.soshuceapi.models.requests.AdoptSearchRequest;
import com.project.soshuceapi.models.requests.AdoptUpdateRequest;
import com.project.soshuceapi.models.requests.AdoptUpdateStatusRequest;

import java.util.List;
import java.util.Map;

public interface IAdoptService {

    Map<String, Object> getAll(AdoptSearchRequest request);

    List<AdoptDTO> getAllByUser(String userId);

    Map<String, Object> getById(String id);

    void create(AdoptCreateRequest request);

    void update(AdoptUpdateRequest request);

    void cancel(String id, String userId);

    void updateStatus(AdoptUpdateStatusRequest request);

    void deleteSoft(String id, String deletedBy);

    Map<String, Long> statisticStatus(String userId);

}
