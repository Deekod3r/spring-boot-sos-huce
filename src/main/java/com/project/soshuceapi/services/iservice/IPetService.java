package com.project.soshuceapi.services.iservice;

import com.project.soshuceapi.models.DTOs.PetDTO;
import com.project.soshuceapi.models.requests.PetCreateRequest;
import com.project.soshuceapi.models.requests.PetUpdateImageRequest;
import com.project.soshuceapi.models.requests.PetUpdateRequest;

import java.util.Map;

public interface IPetService {

    Map<String, Object> getAll(Integer page, Integer limit,
                               String name, String breed, String color, String code,
                               Integer type, Integer age, Integer gender,  Integer status, Integer diet,
                               Integer vaccine, Integer sterilization, Integer rabies, String adoptedBy);

    Map<String, Long> getStatisticCases();

    PetDTO create(PetCreateRequest petCreateRequest);

    PetDTO update(PetUpdateRequest petUpdateRequest);

    PetDTO updateImage(PetUpdateImageRequest request);

    Boolean deleteSoft(String id, String deletedBy);

    PetDTO getById(String id);

}
