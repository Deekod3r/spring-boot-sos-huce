package com.project.soshuceapi.services;

import com.project.soshuceapi.common.Constants;
import com.project.soshuceapi.common.ResponseMessage;
import com.project.soshuceapi.entities.Adopt;
import com.project.soshuceapi.entities.PetCareLog;
import com.project.soshuceapi.entities.logging.ActionLogDetail;
import com.project.soshuceapi.exceptions.BadRequestException;
import com.project.soshuceapi.models.DTOs.ActionLogDTO;
import com.project.soshuceapi.models.DTOs.AdoptDTO;
import com.project.soshuceapi.models.DTOs.PetCareLogDTO;
import com.project.soshuceapi.models.requests.PetCareLogCreateRequest;
import com.project.soshuceapi.models.requests.PetCareLogSearchRequest;
import com.project.soshuceapi.models.requests.PetCareLogUpdateRequest;
import com.project.soshuceapi.repositories.PetCareLogRepo;
import com.project.soshuceapi.services.iservice.IActionLogService;
import com.project.soshuceapi.services.iservice.IAdoptService;
import com.project.soshuceapi.services.iservice.IPetCareLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PetCareLogService implements IPetCareLogService {

    private final String TAG = "ADOPT";

    @Autowired
    private PetCareLogRepo petCareLogRepo;
    @Autowired
    private IActionLogService actionLogService;
    @Autowired
    private IAdoptService adoptionService;

    @Override
    public List<PetCareLogDTO> getAll(PetCareLogSearchRequest request) {
        try {
            List<PetCareLog> petCareLogs = petCareLogRepo.findAll(
                    request.getAdoptId(),
                    request.getPetId(),
                    request.getFromDate(),
                    request.getToDate()
            );
            return petCareLogs.stream().map(this::parsePetCareLogDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void create(PetCareLogCreateRequest request) {
        try {
            AdoptDTO adopt = (AdoptDTO) adoptionService.getById(request.getAdoptId()).get("adopt");
            PetCareLog petCareLog = new PetCareLog();
            petCareLog.setAdopt(Adopt.builder()
                    .id(adopt.getId())
                    .build());
            petCareLog.setDate(request.getDate());
            petCareLog.setNote(request.getNote().trim());
            petCareLog.setCreatedAt(LocalDateTime.now());
            petCareLog.setCreatedBy(request.getCreatedBy());
            petCareLog = petCareLogRepo.save(petCareLog);
            logCreate(petCareLog);
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void update(PetCareLogUpdateRequest request) {
        try {
            PetCareLog petCareLog = petCareLogRepo.findById(request.getId()).orElseThrow(
                    () -> new BadRequestException(ResponseMessage.PetCareLog.NOT_FOUND));
            logUpdate(petCareLog, request);
            petCareLog.setDate(request.getDate());
            petCareLog.setNote(request.getNote().trim());
            petCareLog.setUpdatedAt(LocalDateTime.now());
            petCareLog.setUpdatedBy(request.getUpdatedBy());
            petCareLogRepo.save(petCareLog);
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
            PetCareLog petCareLog = petCareLogRepo.findById(id).orElseThrow(
                    () -> new BadRequestException(ResponseMessage.PetCareLog.NOT_FOUND));
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
            petCareLogRepo.delete(petCareLog);
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public PetCareLogDTO getById(String id) {
        try {
            return petCareLogRepo.findById(id).map(this::parsePetCareLogDTO).orElseThrow(
                    () -> new BadRequestException(ResponseMessage.PetCareLog.NOT_FOUND));
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private PetCareLogDTO parsePetCareLogDTO(PetCareLog petCareLog) {
        return PetCareLogDTO.builder()
                .id(petCareLog.getId())
                .adoptId(petCareLog.getAdopt().getId())
                .adoptCode(petCareLog.getAdopt().getCode())
                .petName(petCareLog.getAdopt().getPet().getCode() + " - " + petCareLog.getAdopt().getPet().getName())
                .date(petCareLog.getDate())
                .note(petCareLog.getNote())
                .build();
    }

    private void logCreate(PetCareLog petCareLog) {
        actionLogService.create(ActionLogDTO.builder()
                .action(Constants.ActionLog.CREATE)
                .description(Constants.ActionLog.CREATE + "." + TAG)
                .createdBy(petCareLog.getCreatedBy())
                .details(List.of(
                        ActionLogDetail.builder()
                            .tableName(TAG)
                            .rowId(petCareLog.getId())
                            .columnName("adopt_id")
                            .oldValue("")
                            .newValue(petCareLog.getAdopt().getId())
                            .build(),
                        ActionLogDetail.builder()
                            .tableName(TAG)
                            .rowId(petCareLog.getId())
                            .columnName("date")
                            .oldValue("")
                            .newValue(petCareLog.getDate().toString())
                            .build(),
                        ActionLogDetail.builder()
                            .tableName(TAG)
                            .rowId(petCareLog.getId())
                            .columnName("note")
                            .oldValue("")
                            .newValue(petCareLog.getNote())
                            .build()
                ))
                .build());
    }

    private void logUpdate(PetCareLog oldValue, PetCareLogUpdateRequest newValue) {
        List<ActionLogDetail> details = new ArrayList<>();
        if (!Objects.equals(oldValue.getDate(), newValue.getDate())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(newValue.getId())
                    .columnName("date")
                    .oldValue(oldValue.getDate().toString())
                    .newValue(newValue.getDate().toString())
                    .build());
        }
        if (!Objects.equals(oldValue.getNote(), newValue.getNote().trim())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(newValue.getId())
                    .columnName("note")
                    .oldValue(oldValue.getNote())
                    .newValue(newValue.getNote().trim())
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
