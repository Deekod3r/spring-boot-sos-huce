package com.project.soshuceapi.services;

import com.project.soshuceapi.entities.Pet;
import com.project.soshuceapi.entities.User;
import com.project.soshuceapi.models.DTOs.PetDTO;
import com.project.soshuceapi.models.mappers.PetMapper;
import com.project.soshuceapi.models.requests.PetCreateRequest;
import com.project.soshuceapi.models.requests.PetUpdateRequest;
import com.project.soshuceapi.repositories.PetRepository;
import com.project.soshuceapi.services.iservice.IFileService;
import com.project.soshuceapi.services.iservice.IPetService;
import com.project.soshuceapi.services.iservice.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import static com.project.soshuceapi.utils.StringUtil.upcaseAllFirstLetters;
import static com.project.soshuceapi.utils.StringUtil.upcaseFirstLetter;

@Service
public class PetService implements IPetService {

    @Autowired
    private PetRepository petRepository;
    @Autowired
    private IFileService fileService;
    @Autowired
    private IUserService userService;
    @Autowired
    private PetMapper petMapper;

    @Override
    public Map<String, Object> getPets(int page, int limit, String name, String breed, String color, String code, Integer type, Integer age, Integer gender, Integer status) {
        Page<Pet> pets = petRepository.findAll(name, breed, color, code, type, age, gender, status, Pageable.ofSize(limit).withPage(page - 1));
        return Map.of(
                "pets", pets.getContent().stream().map(pet -> petMapper.mapTo(pet, PetDTO.class)).collect(Collectors.toList()),
                "total", pets.getTotalElements(),
                "page", pets.getNumber() + 1,
                "limit", pets.getSize(),
                "totalPages", pets.getTotalPages()
        );
    }

    @Override
    public PetDTO create(PetCreateRequest request) {
        try {
            Map<String, String> data = fileService.upload(request.getImage());
            String url = data.get("url");
            request.setBreed(upcaseFirstLetter(request.getBreed()));
            request.setColor(upcaseFirstLetter(request.getColor()));
            request.setName(upcaseAllFirstLetters(request.getName()));
            Pet pet = petMapper.mapFrom(request);
            pet.setImage(url);
            pet.setCode(generateCode(pet.getName()));
            pet.setCreatedAt(LocalDateTime.now());
            pet.setCreatedBy(new User(request.getCreatedBy()));
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

    @Override
    public Map<String, Long> getStatisticCases() {
        return Map.of(
                "total", petRepository.count(),
                "adopted", petRepository.countByStatus(1),
                "wait", petRepository.countByStatus(3),
                "healing", petRepository.countByStatus(2)
        );
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
