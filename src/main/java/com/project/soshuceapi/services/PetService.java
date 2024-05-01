package com.project.soshuceapi.services;

import com.project.soshuceapi.common.Constants;
import com.project.soshuceapi.common.ResponseMessage;
import com.project.soshuceapi.entities.Pet;
import com.project.soshuceapi.entities.User;
import com.project.soshuceapi.entities.logging.ActionLogDetail;
import com.project.soshuceapi.exceptions.BadRequestException;
import com.project.soshuceapi.models.DTOs.ActionLogDTO;
import com.project.soshuceapi.models.DTOs.PetDTO;
import com.project.soshuceapi.models.mappers.PetMapper;
import com.project.soshuceapi.models.requests.PetCreateRequest;
import com.project.soshuceapi.models.requests.PetSearchRequest;
import com.project.soshuceapi.models.requests.PetUpdateImageRequest;
import com.project.soshuceapi.models.requests.PetUpdateRequest;
import com.project.soshuceapi.repositories.PetRepo;
import com.project.soshuceapi.services.iservice.IActionLogService;
import com.project.soshuceapi.services.iservice.IFileService;
import com.project.soshuceapi.services.iservice.IPetService;
import com.project.soshuceapi.utils.DataUtil;
import com.project.soshuceapi.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

import static com.project.soshuceapi.utils.StringUtil.uppercaseAllFirstLetters;
import static com.project.soshuceapi.utils.StringUtil.uppercaseFirstLetter;

@Service
@Slf4j
public class PetService implements IPetService {

    private final static String TAG = "PET";

    @Autowired
    private PetRepo petRepo;
    @Autowired
    private IFileService fileService;
    @Autowired
    private IActionLogService actionLogService;
    @Autowired
    private PetMapper petMapper;


    @Override
    public Map<String, Object> getAll(PetSearchRequest request) {
        try {
            Page<Pet> pets = petRepo.findAll(request.getName(), request.getBreed(), request.getColor(),
                    request.getCode(), request.getType(), request.getAge(), request.getGender(),
                    request.getStatus(), request.getDiet(), request.getVaccine(), request.getSterilization(),
                    request.getRabies(), request.getAdoptedBy(), request.getIntakeDateFrom(), request.getIntakeDateTo(),
                    request.getFullData() ? Pageable.unpaged() : Pageable.ofSize(request.getLimit()).withPage(request.getPage() - 1));
            return Map.of(
                    "pets", pets.getContent().stream()
                        .map(this::parsePetDTO)
                        .toList(),
                    "totalElements", pets.getTotalElements(),
                    "currentPage", pets.getNumber() + 1,
                    "totalPages", pets.getTotalPages()
            );
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void create(PetCreateRequest request) {
        try {
            Map<String, String> data = fileService.upload(request.getImage());
            String url = data.get("url");
            request.setBreed(uppercaseFirstLetter(request.getBreed().trim()));
            request.setColor(uppercaseFirstLetter(request.getColor().trim()));
            request.setName(uppercaseAllFirstLetters(request.getName().trim()));
            request.setDescription(request.getDescription().trim());
            request.setNote(!StringUtil.isNullOrBlank(request.getNote()) ? request.getNote().trim() : request.getNote());
            Pet pet = petMapper.mapFrom(request);
            pet.setImage(url);
            pet.setCreatedAt(LocalDateTime.now());
            pet.setCreatedBy(request.getCreatedBy());
            pet.setCode(generateCode(pet.getName()));
            pet.setIsDeleted(false);
            logCreate(petRepo.save(pet));
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void update(PetUpdateRequest request) {
        try {
            Pet pet = petRepo.findById(request.getId()).orElseThrow(
                    () -> new BadRequestException(ResponseMessage.Pet.NOT_FOUND));
            if (Objects.equals(pet.getStatus(), Constants.PetStatus.ADOPTED)
                    || Objects.equals(pet.getStatus(), Constants.PetStatus.DIED)) {
                throw new BadRequestException(ResponseMessage.Pet.NOT_AVAILABLE_FOR_UPDATE);
            }
            logUpdate(pet, request);
            pet.setBreed(uppercaseFirstLetter(request.getBreed().trim()));
            pet.setColor(uppercaseFirstLetter(request.getColor().trim()));
            pet.setName(uppercaseAllFirstLetters(request.getName().trim()));
            pet.setAge(request.getAge());
            pet.setType(request.getType());
            pet.setIntakeDate(request.getIntakeDate());
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
            pet.setDescription(request.getDescription().trim());
            pet.setNote(!StringUtil.isNullOrBlank(request.getNote()) ? request.getNote().trim() : request.getNote());
            pet.setUpdatedBy(request.getUpdatedBy());
            pet.setUpdatedAt(LocalDateTime.now());
            petRepo.save(pet);
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updateImage(PetUpdateImageRequest request) {
        try {
            Pet pet = petRepo.findById(request.getId()).orElseThrow(
                    () -> new BadRequestException(ResponseMessage.Pet.NOT_FOUND));
            if (Objects.equals(pet.getStatus(), Constants.PetStatus.ADOPTED)
                    || Objects.equals(pet.getStatus(), Constants.PetStatus.DIED)) {
                throw new BadRequestException(ResponseMessage.Pet.NOT_AVAILABLE_FOR_UPDATE);
            }
            Map<String, String> data = fileService.upload(request.getImage());
            String url = data.get("url");
            actionLogService.create(ActionLogDTO.builder()
                    .action(Constants.ActionLog.UPDATE)
                    .description(Constants.ActionLog.UPDATE + "." + TAG)
                    .createdBy(request.getUpdatedBy())
                    .details(List.of(
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(pet.getId())
                                    .columnName("image")
                                    .oldValue(pet.getImage())
                                    .newValue(url)
                                    .build()
                    ))
                    .build());
            pet.setImage(url);
            pet.setUpdatedAt(LocalDateTime.now());
            petRepo.save(pet);
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void delete(String id, String deletedBy) {
        try {
            Pet pet = petRepo.findById(id).orElseThrow(
                    () -> new BadRequestException(ResponseMessage.Pet.NOT_FOUND));
            if (Objects.equals(pet.getStatus(), Constants.PetStatus.ADOPTED)) {
                throw new BadRequestException(ResponseMessage.Pet.NOT_AVAILABLE_FOR_DELETE);
            }
            actionLogService.create(ActionLogDTO.builder()
                    .action(Constants.ActionLog.DELETE_SOFT)
                    .description(Constants.ActionLog.DELETE_SOFT + "." + TAG)
                    .createdBy(deletedBy)
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
            pet.setDeletedAt(LocalDateTime.now());
            pet.setDeletedBy(deletedBy);
            pet.setIsDeleted(true);
            petRepo.save(pet);
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (RuntimeException e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public PetDTO getById(String id) {
        try {
            return petRepo.findById(id).map(this::parsePetDTO)
                    .orElseThrow(() -> new BadRequestException(ResponseMessage.Pet.NOT_FOUND));
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Map<String, Long> getStatisticCases(Boolean compare) {
        try {
            if (compare) {
                LocalDate firstDayOfPreviousMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
                LocalDate lastDayOfPreviousMonth = YearMonth.from(firstDayOfPreviousMonth).atEndOfMonth();
                Map<String, Long> current = new HashMap<>(petRepo.getCountsByStatus(null));
                Map<String, Long> previous = new HashMap<>(petRepo.getCountsByStatus(lastDayOfPreviousMonth));
                current.put("totalPrevious", DataUtil.parseLong(previous.get("total")));
                return current;
            }
            return petRepo.getCountsByStatus(null);
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void setAdoptedBy(String userId, String petId, String updatedBy) {
        try {
            Pet pet = petRepo.findById(petId).orElseThrow(
                    () -> new BadRequestException(ResponseMessage.Pet.NOT_FOUND));
            actionLogService.create(ActionLogDTO.builder()
                    .action(Constants.ActionLog.UPDATE)
                    .description(Constants.ActionLog.UPDATE + "." + TAG)
                    .createdBy(updatedBy)
                    .details(List.of(
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(pet.getId())
                                    .columnName("adopted_by")
                                    .oldValue("")
                                    .newValue(userId)
                                    .build()
                    ))
                    .build());
            pet.setAdoptedBy(User.builder()
                    .id(userId)
                    .build());
            pet.setStatus(Constants.PetStatus.ADOPTED);
            pet.setUpdatedAt(LocalDateTime.now());
            petRepo.save(pet);
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public PetDTO parsePetDTO(Pet pet) {
        PetDTO dto = petMapper.mapTo(pet, PetDTO.class);
        if (Objects.nonNull(pet.getAdoptedBy())) {
            dto.setInfoAdoptedBy(pet.getAdoptedBy().getName()
                    + " - " + pet.getAdoptedBy().getPhoneNumber()
                    + " - " + pet.getAdoptedBy().getEmail());
        }
        return dto;
    }

    private String generateCode(String name) {
        Long seq = petRepo.getSEQ();
        return name.substring(0, 1).toUpperCase() + String.format("%05d", seq);
    }

    private void logCreate(Pet pet) {
        actionLogService.create(ActionLogDTO.builder()
                .action(Constants.ActionLog.CREATE)
                .description(Constants.ActionLog.CREATE + "." + TAG)
                .createdBy(pet.getCreatedBy())
                .details(List.of(
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(pet.getId())
                                .columnName("name")
                                .oldValue("")
                                .newValue(pet.getName().trim())
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(pet.getId())
                                .columnName("type")
                                .oldValue("")
                                .newValue(String.valueOf(pet.getType()))
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(pet.getId())
                                .columnName("gender")
                                .oldValue("")
                                .newValue(String.valueOf(pet.getGender()))
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(pet.getId())
                                .columnName("status")
                                .oldValue("")
                                .newValue(String.valueOf(pet.getStatus()))
                                .build()
                ))
                .build());
    }

    private void logUpdate(Pet oldValue, PetUpdateRequest newValue) {
        List<ActionLogDetail> details = new ArrayList<>();
        if (!Objects.equals(newValue.getBreed().trim(), oldValue.getBreed())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(newValue.getId())
                    .columnName("breed")
                    .oldValue(oldValue.getBreed())
                    .newValue(newValue.getBreed().trim())
                    .build());
        }
        if (!Objects.equals(newValue.getColor().trim(), oldValue.getColor())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(newValue.getId())
                    .columnName("color")
                    .oldValue(oldValue.getColor())
                    .newValue(newValue.getColor().trim())
                    .build());
        }
        if (!Objects.equals(newValue.getName().trim(), oldValue.getName())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(newValue.getId())
                    .columnName("name")
                    .oldValue(oldValue.getName())
                    .newValue(newValue.getName().trim())
                    .build());
        }
        if (!Objects.equals(newValue.getAge(), oldValue.getAge())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(newValue.getId())
                    .columnName("age")
                    .oldValue(String.valueOf(oldValue.getAge()))
                    .newValue(String.valueOf(newValue.getAge()))
                    .build());
        }
        if (!Objects.equals(newValue.getGender(), oldValue.getGender())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(newValue.getId())
                    .columnName("gender")
                    .oldValue(String.valueOf(oldValue.getGender()))
                    .newValue(String.valueOf(newValue.getGender()))
                    .build());
        }
        if (!Objects.equals(newValue.getType(), oldValue.getType())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(newValue.getId())
                    .columnName("type")
                    .oldValue(String.valueOf(oldValue.getType()))
                    .newValue(String.valueOf(newValue.getType()))
                    .build());
        }
        if (!Objects.equals(newValue.getStatus(), oldValue.getStatus())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(newValue.getId())
                    .columnName("status")
                    .oldValue(String.valueOf(oldValue.getStatus()))
                    .newValue(String.valueOf(newValue.getStatus()))
                    .build());
        }
        if (!Objects.equals(newValue.getWeight(), oldValue.getWeight())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(newValue.getId())
                    .columnName("weight")
                    .oldValue(String.valueOf(oldValue.getWeight()))
                    .newValue(String.valueOf(newValue.getWeight()))
                    .build());
        }
        if (!Objects.equals(newValue.getVaccine(), oldValue.getVaccine())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(newValue.getId())
                    .columnName("vaccine")
                    .oldValue(String.valueOf(oldValue.getVaccine()))
                    .newValue(String.valueOf(newValue.getVaccine()))
                    .build());
        }
        if (!Objects.equals(newValue.getSterilization(), oldValue.getSterilization())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(newValue.getId())
                    .columnName("sterilization")
                    .oldValue(String.valueOf(oldValue.getSterilization()))
                    .newValue(String.valueOf(newValue.getSterilization()))
                    .build());
        }
        if (!Objects.equals(newValue.getDiet(), oldValue.getDiet())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(newValue.getId())
                    .columnName("diet")
                    .oldValue(String.valueOf(oldValue.getDiet()))
                    .newValue(String.valueOf(newValue.getDiet()))
                    .build());
        }
        if (!Objects.equals(newValue.getRabies(), oldValue.getRabies())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(newValue.getId())
                    .columnName("rabies")
                    .oldValue(String.valueOf(oldValue.getRabies()))
                    .newValue(String.valueOf(newValue.getRabies()))
                    .build());
        }
        if (!Objects.equals(newValue.getToilet(), oldValue.getToilet())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(newValue.getId())
                    .columnName("toilet")
                    .oldValue(String.valueOf(oldValue.getToilet()))
                    .newValue(String.valueOf(newValue.getToilet()))
                    .build());
        }
        if (!Objects.equals(newValue.getFriendlyToHuman(), oldValue.getFriendlyToHuman())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(newValue.getId())
                    .columnName("friendly_to_human")
                    .oldValue(String.valueOf(oldValue.getFriendlyToHuman()))
                    .newValue(String.valueOf(newValue.getFriendlyToHuman()))
                    .build());
        }
        if (!Objects.equals(newValue.getFriendlyToDogs(), oldValue.getFriendlyToDogs())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(newValue.getId())
                    .columnName("friendly_to_dogs")
                    .oldValue(String.valueOf(oldValue.getFriendlyToDogs()))
                    .newValue(String.valueOf(newValue.getFriendlyToDogs()))
                    .build());
        }
        if (!Objects.equals(newValue.getFriendlyToCats(), oldValue.getFriendlyToCats())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(newValue.getId())
                    .columnName("friendly_to_cats")
                    .oldValue(String.valueOf(oldValue.getFriendlyToCats()))
                    .newValue(String.valueOf(newValue.getFriendlyToCats()))
                    .build());
        }
        if (!Objects.equals(newValue.getDescription().trim(), oldValue.getDescription())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(newValue.getId())
                    .columnName("description")
                    .oldValue(oldValue.getDescription())
                    .newValue(newValue.getDescription().trim())
                    .build());
        }
        if (!details.isEmpty()) {
            actionLogService.create(ActionLogDTO.builder()
                    .action(Constants.ActionLog.UPDATE)
                    .description(Constants.ActionLog.UPDATE + "." + TAG)
                    .createdBy(newValue.getUpdatedBy())
                    .details(details)
                    .build());
        }
    }
}
