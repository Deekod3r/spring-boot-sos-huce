package com.project.soshuceapi.services;

import com.project.soshuceapi.entities.Pet;
import com.project.soshuceapi.models.DTOs.PetDTO;
import com.project.soshuceapi.models.mappers.PetMapper;
import com.project.soshuceapi.models.requests.PetCreateRequest;
import com.project.soshuceapi.models.requests.PetUpdateRequest;
import com.project.soshuceapi.repositories.PetRepository;
import com.project.soshuceapi.services.iservice.IPetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class PetService implements IPetService {

    @Autowired
    private PetRepository petRepository;
    @Autowired
    private FileService fileService;
    @Autowired
    private PetMapper petMapper;

    @Override
    public List<PetDTO> getPets() {
        return null;
    }

    @Override
    public PetDTO create(PetCreateRequest request) {
        try {
            Map<String, String> data = fileService.upload(request.getImage());
            String url = data.get("url");
            Pet pet = petMapper.mapFrom(request);
            pet.setImage(url);
            pet.setCode(generateCode(pet.getName()));
            pet.setCreatedAt(LocalDateTime.now());
            pet = petRepository.save(pet);
            return petMapper.mapTo(pet, PetDTO.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
