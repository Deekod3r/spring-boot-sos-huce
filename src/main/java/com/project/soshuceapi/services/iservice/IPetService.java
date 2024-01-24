package com.project.soshuceapi.services.iservice;

import com.project.soshuceapi.models.DTOs.PetDTO;
import com.project.soshuceapi.models.requests.PetCreateRequest;
import com.project.soshuceapi.models.requests.PetUpdateRequest;

import java.util.List;
import java.util.Map;

public interface IPetService {

    Map<String, Object> getPets(int page, int limit, String name, String breed, String color, String code, Integer type, Integer age, Integer status);

    PetDTO create(PetCreateRequest petCreateRequest);

    PetDTO update(PetUpdateRequest petUpdateRequest);

    PetDTO getById(String id);

}
