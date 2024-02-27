package com.project.soshuceapi.services;

import com.project.soshuceapi.common.Constants;
import com.project.soshuceapi.entities.Pet;
import com.project.soshuceapi.entities.User;
import com.project.soshuceapi.entities.logging.ActionLog;
import com.project.soshuceapi.entities.logging.ActionLogDetail;
import com.project.soshuceapi.exceptions.NotFoundException;
import com.project.soshuceapi.models.DTOs.ActionLogDTO;
import com.project.soshuceapi.models.DTOs.PetDTO;
import com.project.soshuceapi.models.mappers.PetMapper;
import com.project.soshuceapi.models.requests.PetCreateRequest;
import com.project.soshuceapi.models.requests.PetUpdateImageRequest;
import com.project.soshuceapi.models.requests.PetUpdateRequest;
import com.project.soshuceapi.repositories.PetRepository;
import com.project.soshuceapi.services.iservice.IFileService;
import com.project.soshuceapi.services.iservice.IPetService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.project.soshuceapi.utils.StringUtil.upcaseAllFirstLetters;
import static com.project.soshuceapi.utils.StringUtil.upcaseFirstLetter;

@Service
public class PetService implements IPetService {

    private final static String TAG = "PET";

    @Autowired
    private PetRepository petRepository;
    @Autowired
    private IFileService fileService;
    @Autowired
    private PetMapper petMapper;
    @Autowired
    private ActionLogService actionLogService;


    @Override
    public Map<String, Object> getAll(int page, int limit,
                                      String name, String breed, String color, String code,
                                      Integer type, Integer age, Integer gender, Integer status,
                                      Integer diet, Integer vaccine, Integer sterilization, Integer rabies) {
        Page<Pet> pets = petRepository.findAll(name, breed, color, code, type, age, gender, status, diet, vaccine, sterilization, rabies ,Pageable.ofSize(limit).withPage(page - 1));
        List<PetDTO> petDTOs = pets.getContent().stream()
                .map(pet -> {
                    User adoptedBy = pet.getAdoptedBy();
                    PetDTO petDTO = petMapper.mapTo(pet, PetDTO.class);
                    if (Objects.nonNull(adoptedBy)) {
                        petDTO.setAdoptedBy(adoptedBy.getPhoneNumber() + " - " + adoptedBy.getName());
                    }
                    return petDTO;
                })
                .toList();
        return Map.of(
                "pets", petDTOs,
                "total", pets.getTotalElements(),
                "page", pets.getNumber() + 1,
                "limit", pets.getSize(),
                "totalPages", pets.getTotalPages()
        );
    }

    @Override
    @Transactional
    public PetDTO create(PetCreateRequest request) {
        try {
            Map<String, String> data = fileService.upload(request.getImage());
            String url = data.get("url");
            request.setBreed(upcaseFirstLetter(request.getBreed()));
            request.setColor(upcaseFirstLetter(request.getColor()));
            request.setName(upcaseAllFirstLetters(request.getName()));
            Pet pet = petMapper.mapFrom(request);
            pet.setImage(url);
            pet.setCreatedAt(LocalDateTime.now());
            pet.setCreatedBy(request.getCreatedBy());
            pet.setCode(generateCode(pet.getName()));
            pet = petRepository.save(pet);
            logCreate(pet);
            return petMapper.mapTo(pet, PetDTO.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public PetDTO update(PetUpdateRequest request) {
        try {
            Pet pet = petRepository.findById(request.getId()).orElseThrow(() -> new NotFoundException("pet.not.found"));
            Pet oldValue = pet;
            pet.setBreed(upcaseFirstLetter(request.getBreed()));
            pet.setColor(upcaseFirstLetter(request.getColor()));
            pet.setName(upcaseAllFirstLetters(request.getName()));
            pet.setAge(request.getAge());
            pet.setGender(request.getGender());
            pet.setStatus(request.getStatus());
            pet.setWeight(request.getWeight());
            pet.setVaccine(request.getVaccine());
            pet.setSterilization(request.getSterilization());
            pet.setDiet(request.getDiet());
            pet.setRabies(request.getRabies());
            pet.setToilet(request.getToilet());
            pet.setFriendlyToHuman(request.getFriendlyToHuman());
            pet.setFriendlyToDogs(request.getFriendlyToDogs());
            pet.setFriendlyToCats(request.getFriendlyToCats());
            pet.setDescription(request.getDescription());
            pet.setNote(request.getNote());
            pet.setUpdatedBy(request.getUpdatedBy());
            pet.setUpdatedAt(LocalDateTime.now());
            pet = petRepository.save(pet);
            logUpdate(pet, oldValue);
            return petMapper.mapTo(pet, PetDTO.class);
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public PetDTO updateImage(PetUpdateImageRequest request) {
        try {
            Pet pet = petRepository.findById(request.getId()).orElseThrow(() -> new NotFoundException("pet.not.found"));
            String oldImage = pet.getImage();
            Map<String, String> data = fileService.upload(request.getImage());
            String url = data.get("url");
            pet.setImage(url);
            pet.setUpdatedAt(LocalDateTime.now());
            pet = petRepository.save(pet);
            actionLogService.create(ActionLogDTO.builder()
                    .action(Constants.ActionLog.UPDATE)
                    .description(Constants.ActionLog.UPDATE + "." + TAG)
                    .createdBy(request.getUpdatedBy())
                    .details(List.of(
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(pet.getId())
                                    .columnName("image")
                                    .oldValue(oldImage)
                                    .newValue(url)
                                    .build()
                    ))
                    .build());
            return petMapper.mapTo(pet, PetDTO.class);
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public boolean deleteSoft(String id) {
        try {
            petRepository.findById(id).orElseThrow(() -> new NotFoundException("pet.not.found"));
            actionLogService.create(ActionLogDTO.builder()
                    .action(Constants.ActionLog.DELETE)
                    .description(Constants.ActionLog.DELETE + "." + TAG)
                    .createdBy(id)
                    .details(List.of(
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(id)
                                    .columnName("is_deleted")
                                    .oldValue("false")
                                    .newValue("true")
                                    .build()
                    ))
                    .build());
            return petRepository.deleteSoftById(id) > 0;
        } catch (NotFoundException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PetDTO getById(String id) {
        return petRepository.findById(id).map(pet -> petMapper.mapTo(pet, PetDTO.class))
                .orElseThrow(() -> new NotFoundException("pet.not.found"));
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

    private void logCreate(Pet pet) {
        try {
            actionLogService.create(ActionLogDTO.builder()
                    .action(Constants.ActionLog.CREATE)
                    .description(Constants.ActionLog.CREATE + "." + TAG)
                    .createdBy(pet.getCreatedBy())
                    .details(List.of(
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(pet.getId())
                                    .columnName("name")
                                    .newValue(pet.getName())
                                    .build(),
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(pet.getId())
                                    .columnName("type")
                                    .newValue(String.valueOf(pet.getType()))
                                    .build(),
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(pet.getId())
                                    .columnName("gender")
                                    .newValue(String.valueOf(pet.getGender()))
                                    .build(),
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(pet.getId())
                                    .columnName("status")
                                    .newValue(String.valueOf(pet.getStatus()))
                                    .build(),
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(pet.getId())
                                    .columnName("image")
                                    .newValue(pet.getImage())
                                    .build()
                    ))
                    .build());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void logUpdate(Pet newValue, Pet oldValue) {
        try {
            if (Objects.isNull(newValue) || Objects.isNull(oldValue)) {
                throw new RuntimeException("log.update.pet.is.null");
            }
            List<ActionLogDetail> actionLogDetails = new ArrayList<>();
            if (!Objects.equals(newValue.getBreed(), oldValue.getBreed())) {
                actionLogDetails.add(ActionLogDetail.builder()
                        .tableName(TAG)
                        .rowId(newValue.getId())
                        .columnName("breed")
                        .oldValue(oldValue.getBreed())
                        .newValue(newValue.getBreed())
                        .build());
            }
            if (!Objects.equals(newValue.getColor(), oldValue.getColor())) {
                actionLogDetails.add(ActionLogDetail.builder()
                        .tableName(TAG)
                        .rowId(newValue.getId())
                        .columnName("color")
                        .oldValue(oldValue.getColor())
                        .newValue(newValue.getColor())
                        .build());
            }
            if (!Objects.equals(newValue.getName(), oldValue.getName())) {
                actionLogDetails.add(ActionLogDetail.builder()
                        .tableName(TAG)
                        .rowId(newValue.getId())
                        .columnName("name")
                        .oldValue(oldValue.getName())
                        .newValue(newValue.getName())
                        .build());
            }
            if (!Objects.equals(newValue.getAge(), oldValue.getAge())) {
                actionLogDetails.add(ActionLogDetail.builder()
                        .tableName(TAG)
                        .rowId(newValue.getId())
                        .columnName("age")
                        .oldValue(String.valueOf(oldValue.getAge()))
                        .newValue(String.valueOf(newValue.getAge()))
                        .build());
            }
            if (!Objects.equals(newValue.getGender(), oldValue.getGender())) {
                actionLogDetails.add(ActionLogDetail.builder()
                        .tableName(TAG)
                        .rowId(newValue.getId())
                        .columnName("gender")
                        .oldValue(String.valueOf(oldValue.getGender()))
                        .newValue(String.valueOf(newValue.getGender()))
                        .build());
            }
            if (!Objects.equals(newValue.getType(), oldValue.getType())) {
                actionLogDetails.add(ActionLogDetail.builder()
                        .tableName(TAG)
                        .rowId(newValue.getId())
                        .columnName("type")
                        .oldValue(String.valueOf(oldValue.getType()))
                        .newValue(String.valueOf(newValue.getType()))
                        .build());
            }
            if (!Objects.equals(newValue.getStatus(), oldValue.getStatus())) {
                actionLogDetails.add(ActionLogDetail.builder()
                        .tableName(TAG)
                        .rowId(newValue.getId())
                        .columnName("status")
                        .oldValue(String.valueOf(oldValue.getStatus()))
                        .newValue(String.valueOf(newValue.getStatus()))
                        .build());
            }
            if (!Objects.equals(newValue.getWeight(), oldValue.getWeight())) {
                actionLogDetails.add(ActionLogDetail.builder()
                        .tableName(TAG)
                        .rowId(newValue.getId())
                        .columnName("weight")
                        .oldValue(String.valueOf(oldValue.getWeight()))
                        .newValue(String.valueOf(newValue.getWeight()))
                        .build());
            }
            if (!Objects.equals(newValue.getVaccine(), oldValue.getVaccine())) {
                actionLogDetails.add(ActionLogDetail.builder()
                        .tableName(TAG)
                        .rowId(newValue.getId())
                        .columnName("vaccin")
                        .oldValue(String.valueOf(oldValue.getVaccine()))
                        .newValue(String.valueOf(newValue.getVaccine()))
                        .build());
            }
            if (!Objects.equals(newValue.getSterilization(), oldValue.getSterilization())) {
                actionLogDetails.add(ActionLogDetail.builder()
                        .tableName(TAG)
                        .rowId(newValue.getId())
                        .columnName("sterilization")
                        .oldValue(String.valueOf(oldValue.getSterilization()))
                        .newValue(String.valueOf(newValue.getSterilization()))
                        .build());
            }
            if (!Objects.equals(newValue.getDiet(), oldValue.getDiet())) {
                actionLogDetails.add(ActionLogDetail.builder()
                        .tableName(TAG)
                        .rowId(newValue.getId())
                        .columnName("diet")
                        .oldValue(String.valueOf(oldValue.getDiet()))
                        .newValue(String.valueOf(newValue.getDiet()))
                        .build());
            }
            if (!Objects.equals(newValue.getRabies(), oldValue.getRabies())) {
                actionLogDetails.add(ActionLogDetail.builder()
                        .tableName(TAG)
                        .rowId(newValue.getId())
                        .columnName("rabies")
                        .oldValue(String.valueOf(oldValue.getRabies()))
                        .newValue(String.valueOf(newValue.getRabies()))
                        .build());
            }
            if (!Objects.equals(newValue.getToilet(), oldValue.getToilet())) {
                actionLogDetails.add(ActionLogDetail.builder()
                        .tableName(TAG)
                        .rowId(newValue.getId())
                        .columnName("toilet")
                        .oldValue(String.valueOf(oldValue.getToilet()))
                        .newValue(String.valueOf(newValue.getToilet()))
                        .build());
            }
            if (!Objects.equals(newValue.getFriendlyToHuman(), oldValue.getFriendlyToHuman())) {
                actionLogDetails.add(ActionLogDetail.builder()
                        .tableName(TAG)
                        .rowId(newValue.getId())
                        .columnName("friendly_to_human")
                        .oldValue(String.valueOf(oldValue.getFriendlyToHuman()))
                        .newValue(String.valueOf(newValue.getFriendlyToHuman()))
                        .build());
            }
            if (!Objects.equals(newValue.getFriendlyToDogs(), oldValue.getFriendlyToDogs())) {
                actionLogDetails.add(ActionLogDetail.builder()
                        .tableName(TAG)
                        .rowId(newValue.getId())
                        .columnName("friendly_to_dogs")
                        .oldValue(String.valueOf(oldValue.getFriendlyToDogs()))
                        .newValue(String.valueOf(newValue.getFriendlyToDogs()))
                        .build());
            }
            if (!Objects.equals(newValue.getFriendlyToCats(), oldValue.getFriendlyToCats())) {
                actionLogDetails.add(ActionLogDetail.builder()
                        .tableName(TAG)
                        .rowId(newValue.getId())
                        .columnName("friendly_to_cats")
                        .oldValue(String.valueOf(oldValue.getFriendlyToCats()))
                        .newValue(String.valueOf(newValue.getFriendlyToCats()))
                        .build());
            }
            if (!Objects.equals(newValue.getDescription(), oldValue.getDescription())) {
                actionLogDetails.add(ActionLogDetail.builder()
                        .tableName(TAG)
                        .rowId(newValue.getId())
                        .columnName("description")
                        .oldValue(oldValue.getDescription())
                        .newValue(newValue.getDescription())
                        .build());
            }
            if (!Objects.equals(newValue.getNote(), oldValue.getNote())) {
                actionLogDetails.add(ActionLogDetail.builder()
                        .tableName(TAG)
                        .rowId(newValue.getId())
                        .columnName("note")
                        .oldValue(oldValue.getNote())
                        .newValue(newValue.getNote())
                        .build());
            }
            if (!Objects.equals(newValue.getImage(), oldValue.getImage())) {
                actionLogDetails.add(ActionLogDetail.builder()
                        .tableName(TAG)
                        .rowId(newValue.getId())
                        .columnName("image")
                        .oldValue(oldValue.getImage())
                        .newValue(newValue.getImage())
                        .build());
            }
            actionLogService.create(ActionLogDTO.builder()
                    .action(Constants.ActionLog.UPDATE)
                    .description(Constants.ActionLog.UPDATE + "." + TAG)
                    .createdBy(newValue.getUpdatedBy())
                    .details(actionLogDetails)
                    .build());
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
