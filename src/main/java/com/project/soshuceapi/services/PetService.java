package com.project.soshuceapi.services;

import com.project.soshuceapi.models.DTOs.PetDTO;
import com.project.soshuceapi.models.requests.PetCreateRequest;
import com.project.soshuceapi.models.requests.PetUpdateRequest;
import com.project.soshuceapi.repositories.PetRepository;
import com.project.soshuceapi.services.iservice.IPetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetService implements IPetService {

    @Autowired
    private PetRepository petRepository;
    @Autowired
    private FileService fileService;

    @Override
    public List<PetDTO> getPets() {
        return null;
    }

    @Override
    public PetDTO create(PetCreateRequest request) {
        return null;
    }

    @Override
    public PetDTO update(PetUpdateRequest request) {
        return null;
    }

    @Override
    public PetDTO getById(String id) {
        return null;
    }

    private String generateCode(String name) {
        try {
            Long seq = petRepository.getSEQ();
            return name.substring(0, 1).toUpperCase() + String.format("%05d", seq);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
