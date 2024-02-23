package com.project.soshuceapi.services.iservice;

import com.project.soshuceapi.models.DTOs.AdoptDTO;
import com.project.soshuceapi.models.requests.AdoptCreateRequest;
import com.project.soshuceapi.models.requests.AdoptUpdateRequest;

public interface IAdoptService {

    AdoptDTO create(AdoptCreateRequest request);

    AdoptDTO update(AdoptUpdateRequest request);

    boolean deleteSoft(String id);

}
