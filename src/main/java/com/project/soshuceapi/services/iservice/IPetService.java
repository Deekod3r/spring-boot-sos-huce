package com.project.soshuceapi.services.iservice;

import com.project.soshuceapi.models.DTOs.PetDTO;
import com.project.soshuceapi.models.requests.PetCreateRequest;
import com.project.soshuceapi.models.requests.PetUpdateImageRequest;
import com.project.soshuceapi.models.requests.PetUpdateRequest;

import java.util.Map;

public interface IPetService {

    Map<String, Object> getAll(int page, int limit,
                               String name, String breed, String color, String code,
                               Integer type, Integer age, Integer gender,  Integer status, Integer diet,
                               Integer vaccine, Integer sterilization, Integer rabies);

    Map<String, Long> getStatisticCases();

    PetDTO create(PetCreateRequest petCreateRequest);

    PetDTO update(PetUpdateRequest petUpdateRequest);

    PetDTO updateImage(PetUpdateImageRequest request);

    boolean deleteSoft(String id);

    PetDTO getById(String id);

}
