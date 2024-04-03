package com.project.soshuceapi.services.iservice;

import com.project.soshuceapi.models.DTOs.DonateDTO;
import com.project.soshuceapi.models.requests.DonateCreateRequest;
import com.project.soshuceapi.models.requests.DonateSearchRequest;
import com.project.soshuceapi.models.requests.DonateUpdateRequest;

import java.util.Map;

public interface IDonateService {

    Map<String, Object> getAll(DonateSearchRequest request);

    DonateDTO getById(String id);

    void create(DonateCreateRequest request);

    void update(DonateUpdateRequest request);

    void delete(String id, String deletedBy);

}
