package com.project.soshuceapi.services.iservice;

import com.project.soshuceapi.entities.Pet;
import com.project.soshuceapi.models.DTOs.PetDTO;
import com.project.soshuceapi.models.requests.PetCreateRequest;
import com.project.soshuceapi.models.requests.PetSearchRequest;
import com.project.soshuceapi.models.requests.PetUpdateImageRequest;
import com.project.soshuceapi.models.requests.PetUpdateRequest;

import java.util.Map;

public interface IPetService {

    Map<String, Object> getAll(PetSearchRequest request);

    Map<String, Long> getStatisticCases(Boolean compare);

    PetDTO getById(String id);

    void create(PetCreateRequest petCreateRequest);

    void update(PetUpdateRequest petUpdateRequest);

    void updateImage(PetUpdateImageRequest request);

    void delete(String id, String deletedBy);

    void setAdoptedBy(String userId, String petId, String updatedBy);

    PetDTO parsePetDTO(Pet pet);

}
