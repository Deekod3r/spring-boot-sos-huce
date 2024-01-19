package com.project.soshuceapi.services.iservice;

import com.project.soshuceapi.entities.Pet;
import com.project.soshuceapi.models.DTOs.PetDTO;
import com.project.soshuceapi.models.requests.PetCreateRequest;
import com.project.soshuceapi.models.requests.PetUpdateRequest;

import java.util.List;

public interface IPetService {

    List<PetDTO> getPets();

    PetDTO create(PetCreateRequest petCreateRequest);

    PetDTO update(PetUpdateRequest petUpdateRequest);

    PetDTO getById(String id);

}
