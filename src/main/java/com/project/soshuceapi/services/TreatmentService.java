package com.project.soshuceapi.services;

import com.project.soshuceapi.common.Constants;
import com.project.soshuceapi.common.ResponseMessage;
import com.project.soshuceapi.entities.Pet;
import com.project.soshuceapi.entities.Treatment;
import com.project.soshuceapi.entities.logging.ActionLogDetail;
import com.project.soshuceapi.exceptions.BadRequestException;
import com.project.soshuceapi.models.DTOs.ActionLogDTO;
import com.project.soshuceapi.models.DTOs.TotalAmountStatisticDTO;
import com.project.soshuceapi.models.DTOs.TreatmentDTO;
import com.project.soshuceapi.models.requests.*;
import com.project.soshuceapi.repositories.ImageRepo;
import com.project.soshuceapi.repositories.PetRepo;
import com.project.soshuceapi.repositories.TreatmentRepo;
import com.project.soshuceapi.services.iservice.IActionLogService;
import com.project.soshuceapi.services.iservice.IFileService;
import com.project.soshuceapi.services.iservice.ITreatmentService;
import com.project.soshuceapi.utils.DataUtil;
import com.project.soshuceapi.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class TreatmentService implements ITreatmentService {

    private final static String TAG = "TREATMENT";

    @Autowired
    private TreatmentRepo treatmentRepo;
    @Autowired
    private PetRepo petRepo;
    @Autowired
    private ImageRepo imageRepo;
    @Autowired
    private IFileService fileService;
    @Autowired
    private IActionLogService actionLogService;

    @Override
    public Map<String, Object> getAll(TreatmentSearchRequest request) {
        try {
            Page<Treatment> treatments = treatmentRepo.findAll(
                    request.getPetId(),
                    request.getStatus(),
                    request.getType(),
                    Objects.nonNull(request.getDaysOfTreatment()) ? Duration.ofDays(request.getDaysOfTreatment()) : null,
                    request.getFullData() ? Pageable.unpaged() : Pageable.ofSize(request.getLimit()).withPage(request.getPage() - 1)
            );
            return Map.of(
                    "treatments", treatments.getContent().stream().map(treatment -> parseTreatmentDTO(treatment, false)).toList(),
                    "totalElements", treatments.getTotalElements(),
                    "totalPages", treatments.getTotalPages(),
                    "currentPage", treatments.getNumber() + 1
            );
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public TreatmentDTO getById(String id) {
        try {
            Treatment treatment = treatmentRepo.findById(id)
                    .orElseThrow(() -> new BadRequestException(ResponseMessage.Treatment.NOT_FOUND));
            return parseTreatmentDTO(treatment, true);
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<TotalAmountStatisticDTO> getTotalTreatmentCost(TotalTreatmentCostSearchRequest request) {
        try {
            List<Object[]> data = treatmentRepo.calTotalTreatmentCost(request.getYear());
            return data.stream()
                    .map(d -> TotalAmountStatisticDTO.builder()
                            .year(DataUtil.parseInteger(d[0].toString()))
                            .month(DataUtil.parseInteger(d[1].toString()))
                            .totalAmount(DataUtil.parseBigDecimal(d[2].toString()))
                            .build())
                    .toList();
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<TotalAmountStatisticDTO> getTotalTreatmentCostByType(TotalTreatmentCostSearchRequest request) {
        try {
            List<Object[]> data = null;
            if (Objects.nonNull(request.getMonth())) {
                data = treatmentRepo.calTotalTreatmentCostByTypeAndMonth(request.getYear(), request.getMonth());
                return data.stream()
                        .map(d -> TotalAmountStatisticDTO.builder()
                                .year(DataUtil.parseInteger(d[0].toString()))
                                .month(DataUtil.parseInteger(d[1].toString()))
                                .totalAmount(DataUtil.parseBigDecimal(d[2].toString()))
                                .category(DataUtil.parseInteger(d[3].toString()))
                                .build())
                        .toList();
            } else {
                data = treatmentRepo.calTotalTreatmentCostByType(request.getYear());
                return data.stream()
                        .map(d -> TotalAmountStatisticDTO.builder()
                                .year(DataUtil.parseInteger(d[0].toString()))
                                .totalAmount(DataUtil.parseBigDecimal(d[1].toString()))
                                .category(DataUtil.parseInteger(d[2].toString()))
                                .build())
                        .toList();
            }
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void create(TreatmentCreateRequest request) {
        try {
            List<Treatment> treatments = new ArrayList<>();
            request.getDetailPet().forEach(petDetail -> {
                    Treatment treatment = new Treatment();
                    Pet pet = petRepo.findById(request.getPetId())
                            .orElseThrow(() -> new BadRequestException(ResponseMessage.Pet.NOT_FOUND));
                    if (Objects.equals(pet.getStatus(), Constants.PetStatus.ADOPTED)) {
                        throw new BadRequestException(ResponseMessage.Pet.ADOPTED);
                    }
                    if (Objects.equals(pet.getStatus(), Constants.PetStatus.DIED)) {
                        throw new BadRequestException(ResponseMessage.Pet.DIED);
                    }
                    treatment.setPet(pet);
                    treatment.setLocation(request.getLocation().trim());
                    treatment.setName(petDetail.getName().trim());
                    treatment.setStartDate(petDetail.getStartDate());
                    treatment.setEndDate(petDetail.getEndDate());
                    treatment.setType(petDetail.getType());
                    treatment.setStatus(true);
                    treatment.setDescription(!StringUtil.isNullOrBlank(petDetail.getDescription())
                            ? petDetail.getDescription().trim() : petDetail.getDescription());
                    treatment.setPrice(petDetail.getPrice());
                    treatment.setQuantity(petDetail.getQuantity());
                    treatment.setCreatedBy(request.getCreatedBy());
                    treatment.setCreatedAt(LocalDateTime.now());
                    treatment.setIsDeleted(false);
                    treatments.add(treatment);
            });
            List<Treatment> result = treatmentRepo.saveAll(treatments);
            for (Treatment treatment : result) {
                fileService.createImage(ImageCreateRequest.of(
                        treatment.getName(),
                        treatment.getId(),
                        request.getImages(),
                        request.getCreatedBy()));
                logCreate(treatment);
            }
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void update(TreatmentUpdateRequest request) {
        try {
            Treatment treatment = treatmentRepo.findById(request.getId())
                    .orElseThrow(() -> new BadRequestException(ResponseMessage.Treatment.NOT_FOUND));
            logUpdate(treatment, request);
            treatment.setName(request.getName().trim());
            treatment.setStartDate(request.getStartDate());
            treatment.setEndDate(request.getEndDate());
            treatment.setLocation(request.getLocation().trim());
            treatment.setDescription(!StringUtil.isNullOrBlank(request.getDescription())
                    ? request.getDescription().trim() : request.getDescription());
            treatment.setPrice(request.getPrice());
            treatment.setQuantity(request.getQuantity());
            treatment.setType(request.getType());
            treatment.setStatus(request.getStatus());
            treatment.setUpdatedBy(request.getUpdatedBy());
            treatment.setUpdatedAt(LocalDateTime.now());
            treatmentRepo.save(treatment);
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
            Treatment treatment = treatmentRepo.findById(id)
                    .orElseThrow(() -> new BadRequestException(ResponseMessage.Treatment.NOT_FOUND));
            treatment.setIsDeleted(true);
            treatment.setDeletedBy(deletedBy);
            treatment.setDeletedAt(LocalDateTime.now());
            treatmentRepo.save(treatment);
            actionLogService.create(ActionLogDTO.builder()
                    .action(Constants.ActionLog.DELETE_SOFT)
                    .description(Constants.ActionLog.DELETE_SOFT + "." + TAG)
                    .createdBy(deletedBy)
                    .details(List.of(
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(treatment.getId())
                                    .columnName("is_deleted")
                                    .oldValue("false")
                                    .newValue("true")
                                    .build()
                    ))
                    .build());
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private TreatmentDTO parseTreatmentDTO(Treatment treatment, Boolean withImages) {
        return TreatmentDTO.of(
                treatment.getId(),
                treatment.getName(),
                treatment.getStartDate(),
                treatment.getEndDate(),
                treatment.getType(),
                treatment.getLocation(),
                treatment.getDescription(),
                treatment.getPrice(),
                treatment.getQuantity(),
                treatment.getStatus(),
                treatment.getPet().getId(),
                treatment.getPet().getName(),
                treatment.getPet().getCode(),
                withImages ? imageRepo.findByObjectId(treatment.getId()) : null
        );
    }

    private void logCreate(Treatment treatment) {
        actionLogService.create(ActionLogDTO.builder()
                .action(Constants.ActionLog.CREATE)
                .description(Constants.ActionLog.CREATE + "." + TAG)
                .createdBy(treatment.getCreatedBy())
                .details(List.of(
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(treatment.getId())
                                .columnName("name")
                                .oldValue("")
                                .newValue(treatment.getName().trim())
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(treatment.getId())
                                .columnName("startDate")
                                .oldValue("")
                                .newValue(treatment.getStartDate().toString())
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(treatment.getId())
                                .columnName("endDate")
                                .oldValue("")
                                .newValue(treatment.getEndDate().toString())
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(treatment.getId())
                                .columnName("location")
                                .oldValue("")
                                .newValue(treatment.getLocation().trim())
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(treatment.getId())
                                .columnName("description")
                                .oldValue("")
                                .newValue(!StringUtil.isNullOrBlank(treatment.getDescription())
                                        ? treatment.getDescription().trim() : treatment.getDescription())
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(treatment.getId())
                                .columnName("price")
                                .oldValue("")
                                .newValue(treatment.getPrice().toString())
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(treatment.getId())
                                .columnName("quantity")
                                .oldValue("")
                                .newValue(treatment.getQuantity().toString())
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(treatment.getId())
                                .columnName("pet_id")
                                .oldValue("")
                                .newValue(treatment.getPet().getId())
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(treatment.getId())
                                .columnName("type")
                                .oldValue("")
                                .newValue(String.valueOf(treatment.getType()))
                                .build()
                ))
                .build());
    }

    private void logUpdate(Treatment oldValue, TreatmentUpdateRequest newValue) {
        List<ActionLogDetail> details = new ArrayList<>();
        if (!Objects.equals(oldValue.getName(), newValue.getName().trim())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(oldValue.getId())
                    .columnName("name")
                    .oldValue(oldValue.getName())
                    .newValue(newValue.getName())
                    .build());
        }
        if (!Objects.equals(oldValue.getStartDate(), newValue.getStartDate())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(oldValue.getId())
                    .columnName("startDate")
                    .oldValue(oldValue.getStartDate().toString())
                    .newValue(newValue.getStartDate().toString())
                    .build());
        }
        if (!Objects.equals(oldValue.getEndDate(), newValue.getEndDate())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(oldValue.getId())
                    .columnName("endDate")
                    .oldValue(oldValue.getEndDate().toString())
                    .newValue(newValue.getEndDate().toString())
                    .build());
        }
        if (!Objects.equals(oldValue.getLocation(), newValue.getLocation().trim())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(oldValue.getId())
                    .columnName("location")
                    .oldValue(oldValue.getLocation())
                    .newValue(newValue.getLocation())
                    .build());
        }
        if (!Objects.equals(oldValue.getPrice(), newValue.getPrice())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(oldValue.getId())
                    .columnName("price")
                    .oldValue(oldValue.getPrice().toString())
                    .newValue(newValue.getPrice().toString())
                    .build());
        }
        if (!Objects.equals(oldValue.getQuantity(), newValue.getQuantity())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(oldValue.getId())
                    .columnName("quantity")
                    .oldValue(oldValue.getQuantity().toString())
                    .newValue(newValue.getQuantity().toString())
                    .build());
        }
        if (!Objects.equals(oldValue.getType(), newValue.getType())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(oldValue.getId())
                    .columnName("type")
                    .oldValue(String.valueOf(oldValue.getType()))
                    .newValue(String.valueOf(newValue.getType()))
                    .build());
        }
        if (!Objects.equals(oldValue.getStatus(), newValue.getStatus())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(oldValue.getId())
                    .columnName("status")
                    .oldValue(oldValue.getStatus().toString())
                    .newValue(newValue.getStatus().toString())
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
