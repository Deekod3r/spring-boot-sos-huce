package com.project.soshuceapi.services.iservice;

import com.project.soshuceapi.models.DTOs.AdoptDTO;
import com.project.soshuceapi.models.requests.AdoptCreateRequest;
import com.project.soshuceapi.models.requests.AdoptUpdateRequest;

import java.util.List;

public interface IAdoptService {

    List<AdoptDTO> getAll();

    AdoptDTO create(AdoptCreateRequest request);

    AdoptDTO update(AdoptUpdateRequest request);

    Boolean deleteSoft(String id);

}
