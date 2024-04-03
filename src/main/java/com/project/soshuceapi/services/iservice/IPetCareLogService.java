package com.project.soshuceapi.services.iservice;

import com.project.soshuceapi.models.DTOs.PetCareLogDTO;
import com.project.soshuceapi.models.requests.PetCareLogCreateRequest;
import com.project.soshuceapi.models.requests.PetCareLogSearchRequest;
import com.project.soshuceapi.models.requests.PetCareLogUpdateRequest;

import java.util.List;

public interface IPetCareLogService {

    List<PetCareLogDTO> getAll(PetCareLogSearchRequest request);

    void create(PetCareLogCreateRequest request);

    void update(PetCareLogUpdateRequest request);

    void delete(String id, String deletedBy);

    PetCareLogDTO getById(String id);

}
