package com.project.soshuceapi.services;

import com.project.soshuceapi.common.Constants;
import com.project.soshuceapi.common.ResponseMessage;
import com.project.soshuceapi.entities.Adopt;
import com.project.soshuceapi.entities.Pet;
import com.project.soshuceapi.entities.User;
import com.project.soshuceapi.entities.locations.Ward;
import com.project.soshuceapi.entities.logging.ActionLogDetail;
import com.project.soshuceapi.exceptions.BadRequestException;
import com.project.soshuceapi.models.DTOs.ActionLogDTO;
import com.project.soshuceapi.models.DTOs.AdoptDTO;
import com.project.soshuceapi.models.DTOs.AdoptLogDTO;
import com.project.soshuceapi.models.DTOs.TotalAmountStatisticDTO;
import com.project.soshuceapi.models.requests.*;
import com.project.soshuceapi.repositories.AdoptRepo;
import com.project.soshuceapi.repositories.PetRepo;
import com.project.soshuceapi.services.iservice.IActionLogService;
import com.project.soshuceapi.services.iservice.IAdoptService;
import com.project.soshuceapi.services.iservice.ILocationService;
import com.project.soshuceapi.services.iservice.IPetService;
import com.project.soshuceapi.utils.DataUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;


@Service
@Slf4j
public class AdoptService implements IAdoptService {

    private final String TAG = "ADOPT";

    @Autowired
    private AdoptRepo adoptRepo;
    @Autowired
    private IActionLogService actionLogService;
    @Autowired
    private ILocationService locationService;
    @Autowired
    private IPetService petService;
    @Autowired
    private PetRepo petRepo;

    @Override
    public Map<String, Object> getAll(AdoptSearchRequest request) {
        try {
            Page<Object[]> adopts = adoptRepo.findAll(
                    request.getStatus(), request.getCode(),
                    DataUtil.parseLocalDateTime(request.getFromDate()),
                    DataUtil.parseLocalDateTime(request.getToDate()),
                    request.getRegisteredBy(), request.getPetAdopt(),
                    request.getFullData() ? Pageable.unpaged() : Pageable.ofSize(request.getLimit()).withPage(request.getPage() - 1)
            );
            List<AdoptDTO> adoptDTOS = adopts.getContent().stream()
                    .map(adopt -> {
                        AdoptDTO adoptDTO = new AdoptDTO();
                        adoptDTO.setId(DataUtil.parseString(adopt[0]));
                        adoptDTO.setCode(DataUtil.parseString(adopt[1]));
                        adoptDTO.setWardId(DataUtil.parseInteger(adopt[2]));
                        adoptDTO.setDistrictId(DataUtil.parseInteger(adopt[3]));
                        adoptDTO.setProvinceId(DataUtil.parseInteger(adopt[4]));
                        adoptDTO.setFee(DataUtil.parseBigDecimal(adopt[5]));
                        adoptDTO.setAddress(DataUtil.parseString(adopt[6]));
                        adoptDTO.setStatus(DataUtil.parseInteger(adopt[7]));
                        adoptDTO.setReason(DataUtil.parseString(adopt[8]));
                        adoptDTO.setConfirmedAt(DataUtil.parseLocalDateTime(adopt[9]));
                        adoptDTO.setRejectedAt(DataUtil.parseLocalDateTime(adopt[10]));
                        adoptDTO.setRejectedReason(DataUtil.parseString(adopt[11]));
                        adoptDTO.setCreatedAt(DataUtil.parseLocalDateTime(adopt[12]));
                        adoptDTO.setPetId(DataUtil.parseString(adopt[13]));
                        adoptDTO.setPetName(DataUtil.parseString(adopt[14]));
                        adoptDTO.setCreatedBy(DataUtil.parseString(adopt[15]));
                        adoptDTO.setNameCreatedBy(DataUtil.parseString(adopt[16]));
                        adoptDTO.setRegisteredBy(DataUtil.parseString(adopt[17]));
                        adoptDTO.setNameRegisteredBy(DataUtil.parseString(adopt[18]));
                        adoptDTO.setEmailRegisteredBy(DataUtil.parseString(adopt[19]));
                        adoptDTO.setPhoneRegisteredBy(DataUtil.parseString(adopt[20]));
                        adoptDTO.setConfirmedBy(DataUtil.parseString(adopt[21]));
                        adoptDTO.setNameConfirmedBy(DataUtil.parseString(adopt[22]));
                        adoptDTO.setRejectedBy(DataUtil.parseString(adopt[23]));
                        adoptDTO.setNameRejectedBy(DataUtil.parseString(adopt[24]));
                        adoptDTO.setWardName(DataUtil.parseString(adopt[25]));
                        adoptDTO.setDistrictName(DataUtil.parseString(adopt[26]));
                        adoptDTO.setProvinceName(DataUtil.parseString(adopt[27]));
                        return adoptDTO;
                    })
                    .toList();
            return Map.of(
                    "adopts", adoptDTOS,
                    "totalElements", adopts.getTotalElements(),
                    "currentPage", adopts.getNumber() + 1,
                    "totalPages", adopts.getTotalPages()
            );
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getById(String id) {
        try {
            Adopt adopt = adoptRepo.findById(id).orElseThrow(
                    () -> new BadRequestException(ResponseMessage.Adopt.NOT_FOUND));
            return new HashMap<>() {{
                put("adopt", parseAdoptDTO(adopt));
                put("pet", petService.parsePetDTO(adopt.getPet()));
            }};
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void create(AdoptCreateRequest request) {
        try {
            Pet pet = petRepo.findById(request.getPetId()).orElseThrow(
                    () -> new BadRequestException(ResponseMessage.Pet.NOT_FOUND));
            if (!Objects.equals(pet.getStatus(), Constants.PetStatus.WAIT_FOR_ADOPTING)) {
                throw new BadRequestException(ResponseMessage.Pet.NOT_AVAILABLE_FOR_ADOPT);
            }
            long count = adoptRepo.countByStatus(
                    List.of(Constants.AdoptStatus.WAIT_FOR_PROGRESSING, Constants.AdoptStatus.IN_PROGRESS),
                    request.getRegisteredBy());
            if (count > 3) {
                throw new BadRequestException(ResponseMessage.Adopt.MAX_ADOPTS);
            }
            if (Boolean.TRUE.equals(checkDuplicate(request.getPetId(), request.getRegisteredBy()))) {
                throw new BadRequestException(ResponseMessage.Adopt.DUPLICATE_ADOPT);
            }
            Adopt adopt = new Adopt();
            adopt.setPet(pet);
            adopt.setCreatedBy(User.builder()
                    .id(request.getCreatedBy())
                    .build());
            adopt.setRegisteredBy(User.builder()
                    .id(request.getRegisteredBy())
                    .build());
            adopt.setCode(generateCode());
            adopt.setCreatedAt(LocalDateTime.now());
            adopt.setWardId(request.getWardId());
            adopt.setDistrictId(request.getDistrictId());
            adopt.setProvinceId(request.getProvinceId());
            adopt.setAddress(request.getAddress().trim());
            adopt.setReason(request.getReason().trim());
            adopt.setStatus(Constants.AdoptStatus.WAIT_FOR_PROGRESSING);
            adopt.setIsDeleted(false);
            adopt.setFee(BigDecimal.ZERO);
            logCreate(adoptRepo.save(adopt));
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void update(AdoptUpdateRequest request) {
        try {
            Adopt adopt = adoptRepo.findById(request.getId()).orElseThrow(
                    () -> new BadRequestException(ResponseMessage.Adopt.NOT_FOUND));
            if (!Objects.equals(adopt.getStatus(), Constants.AdoptStatus.WAIT_FOR_PROGRESSING)
                && !Objects.equals(adopt.getStatus(), Constants.AdoptStatus.IN_PROGRESS)) {
                throw new BadRequestException(ResponseMessage.Adopt.NOT_AVAILABLE_FOR_UPDATE);
            }
            logUpdate(adopt, request);
            adopt.setWardId(request.getWardId());
            adopt.setDistrictId(request.getDistrictId());
            adopt.setProvinceId(request.getProvinceId());
            adopt.setAddress(request.getAddress().trim());
            adopt.setReason(request.getReason().trim());
            adopt.setUpdatedAt(LocalDateTime.now());
            adopt.setUpdatedBy(request.getUpdatedBy());
            adoptRepo.save(adopt);
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void cancel(String id, String userId) {
        try {
            Adopt adopt = adoptRepo.findById(id).orElseThrow(
                    () -> new BadRequestException(ResponseMessage.Adopt.NOT_FOUND));
            if (!Objects.equals(adopt.getRegisteredBy().getId(), userId)) {
                throw new BadRequestException(ResponseMessage.Authentication.PERMISSION_DENIED);
            }
            if (Objects.equals(adopt.getStatus(), Constants.AdoptStatus.WAIT_FOR_PROGRESSING)) {
                actionLogService.create(ActionLogDTO.builder()
                        .action(Constants.ActionLog.UPDATE)
                        .description(Constants.ActionLog.UPDATE + "." + TAG)
                        .createdBy(userId)
                        .details(List.of(
                                ActionLogDetail.builder()
                                        .tableName(TAG)
                                        .rowId(adopt.getId())
                                        .columnName("status")
                                        .oldValue(String.valueOf(adopt.getStatus()))
                                        .newValue(String.valueOf(Constants.AdoptStatus.CANCEL))
                                        .build()
                        ))
                        .build());
                adopt.setStatus(Constants.AdoptStatus.CANCEL);
                adopt.setUpdatedAt(LocalDateTime.now());
                adoptRepo.save(adopt);
            } else {
                throw new BadRequestException(ResponseMessage.Adopt.NOT_AVAILABLE_FOR_CANCEL);
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
    public void updateStatus(AdoptUpdateStatusRequest request) {
        try {
            Adopt adopt = adoptRepo.findById(request.getId()).orElseThrow(
                    () -> new BadRequestException(ResponseMessage.Adopt.NOT_FOUND));
            if (Objects.equals(adopt.getStatus(), request.getStatus())) {
                return;
            }
            if (!Objects.equals(adopt.getStatus(), Constants.AdoptStatus.WAIT_FOR_PROGRESSING)
                    && !Objects.equals(adopt.getStatus(), Constants.AdoptStatus.IN_PROGRESS)) {
                throw new BadRequestException(ResponseMessage.Adopt.NOT_AVAILABLE_FOR_UPDATE);
            }
            if (Objects.equals(request.getStatus(), Constants.AdoptStatus.COMPLETE)) {
                if (Objects.equals(adopt.getPet().getStatus(), Constants.PetStatus.DIED)) {
                    throw new BadRequestException(ResponseMessage.Pet.DIED);
                }
                if (Objects.equals(adopt.getPet().getStatus(), Constants.PetStatus.ADOPTED)) {
                    throw new BadRequestException(ResponseMessage.Pet.ADOPTED);
                }
            }
            actionLogService.create(ActionLogDTO.builder()
                    .action(Constants.ActionLog.UPDATE)
                    .description(Constants.ActionLog.UPDATE + "." + TAG)
                    .createdBy(request.getUpdatedBy())
                    .details(List.of(
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(adopt.getId())
                                    .columnName("status")
                                    .oldValue(String.valueOf(adopt.getStatus()))
                                    .newValue(String.valueOf(request.getStatus()))
                                    .build()
                    ))
                    .build());
            adopt.setStatus(request.getStatus());
            adopt.setUpdatedAt(LocalDateTime.now());
            adopt.setUpdatedBy(request.getUpdatedBy());
            if (Objects.equals(request.getStatus(), Constants.AdoptStatus.COMPLETE)) {
                adopt.setConfirmedBy(User.builder()
                        .id(request.getUpdatedBy())
                        .build());
                adopt.setConfirmedAt(LocalDateTime.now());
                adopt.setFee(request.getFee());
                petService.setAdoptedBy(adopt.getRegisteredBy().getId(), adopt.getPet().getId(), request.getUpdatedBy());
            }
            if (Objects.equals(request.getStatus(), Constants.AdoptStatus.REJECT)) {
                adopt.setRejectedBy(User.builder()
                        .id(request.getUpdatedBy())
                        .build());
                adopt.setRejectedAt(LocalDateTime.now());
                adopt.setRejectedReason( request.getMessage().trim());
            }
            adoptRepo.save(adopt);
            if (Objects.equals(request.getStatus(), Constants.AdoptStatus.COMPLETE)) {
                adoptRepo.rejectAllByPet(adopt.getPet().getId(), request.getUpdatedBy());
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
    public void delete(String id, String deletedBy) {
        try {
            Adopt adopt = adoptRepo.findById(id).orElseThrow(
                    () -> new BadRequestException(ResponseMessage.Adopt.NOT_FOUND));
            if (Objects.equals(adopt.getStatus(), Constants.AdoptStatus.COMPLETE)) {
                throw new BadRequestException(ResponseMessage.Adopt.NOT_AVAILABLE_FOR_DELETE);
            }
            actionLogService.create(ActionLogDTO.builder()
                    .action(Constants.ActionLog.DELETE_SOFT)
                    .description(Constants.ActionLog.DELETE_SOFT + "." + TAG)
                    .createdBy(deletedBy)
                    .details(List.of(
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(adopt.getId())
                                    .columnName("is_deleted")
                                    .oldValue("false")
                                    .newValue("true")
                                    .build()
                    ))
                    .build());
            adopt.setIsDeleted(true);
            adopt.setDeletedAt(LocalDateTime.now());
            adopt.setDeletedBy(deletedBy);
            adoptRepo.save(adopt);
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Map<String, Long> statisticStatus(String userId) {
        try {
            return adoptRepo.countAll(userId);
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<TotalAmountStatisticDTO> getTotalFeeAdopt(TotalFeeAdoptSearchRequest request) {
        try {
            List<Object[]> data = adoptRepo.calTotalFeeAdopt(request.getYear());
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
    public List<AdoptLogDTO> getAdoptsNearLog() {
        try {
            List<Object[]> data = adoptRepo.findAdoptsNearLog();
            return data.stream()
                    .map(d -> AdoptLogDTO.builder()
                            .id(d[0].toString())
                            .code(d[1].toString())
                            .nameRegister(d[2].toString())
                            .phoneRegister(d[3].toString())
                            .emailRegister(d[4].toString())
                            .checkDateFirst(Objects.requireNonNull(DataUtil.parseLocalDateTime(d[5].toString(),
                                    Constants.FormatPattern.LOCAL_DATETIME_WITH_NANOSECONDS)).toLocalDate())
                            .checkDateSecond(Objects.requireNonNull(DataUtil.parseLocalDateTime(d[6].toString(),
                                    Constants.FormatPattern.LOCAL_DATETIME_WITH_NANOSECONDS)).toLocalDate())
                            .checkDateThird(Objects.requireNonNull(DataUtil.parseLocalDateTime(d[7].toString(),
                                    Constants.FormatPattern.LOCAL_DATETIME_WITH_NANOSECONDS)).toLocalDate())
                            .build())
                    .toList();
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private Boolean checkDuplicate(String petId, String userId) {
        long count = adoptRepo.checkDuplicate(petId, userId);
        return count > 0;
    }

    private String generateCode() {
        Long seq = adoptRepo.getSEQ();
        return "ADT" + String.format("%05d", seq);
    }

    private AdoptDTO parseAdoptDTO(Adopt adopt) {
        AdoptDTO adoptDTO = new AdoptDTO();
        adoptDTO.setId(adopt.getId());
        adoptDTO.setCode(adopt.getCode());
        adoptDTO.setWardId(adopt.getWardId());
        adoptDTO.setDistrictId(adopt.getDistrictId());
        adoptDTO.setProvinceId(adopt.getProvinceId());
        adoptDTO.setFee(adopt.getFee());
        adoptDTO.setAddress(adopt.getAddress());
        adoptDTO.setStatus(adopt.getStatus());
        adoptDTO.setReason(adopt.getReason());
        adoptDTO.setConfirmedAt(adopt.getConfirmedAt());
        adoptDTO.setRejectedAt(adopt.getRejectedAt());
        adoptDTO.setRejectedReason(adopt.getRejectedReason());
        adoptDTO.setCreatedAt(adopt.getCreatedAt());

        adoptDTO.setPetId(adopt.getPet().getId());
        adoptDTO.setPetName(adopt.getPet().getCode() + " - " + adopt.getPet().getName());
        adoptDTO.setCreatedBy(adopt.getCreatedBy().getId());
        adoptDTO.setNameCreatedBy(adopt.getCreatedBy().getRole() + " - " + adopt.getCreatedBy().getName());
        adoptDTO.setRegisteredBy(adopt.getRegisteredBy().getId());
        adoptDTO.setNameRegisteredBy(adopt.getRegisteredBy().getName());
        adoptDTO.setEmailRegisteredBy(adopt.getRegisteredBy().getEmail());
        adoptDTO.setPhoneRegisteredBy(adopt.getRegisteredBy().getPhoneNumber());
        if (Objects.nonNull(adopt.getConfirmedBy())) {
            adoptDTO.setConfirmedBy(adopt.getConfirmedBy().getId());
            adoptDTO.setNameConfirmedBy(adopt.getConfirmedBy().getName());
        }
        if (Objects.nonNull(adopt.getRejectedBy())) {
            adoptDTO.setRejectedBy(adopt.getRejectedBy().getId());
            adoptDTO.setNameRejectedBy(adopt.getRejectedBy().getName());
        }
        Ward ward = locationService.getWardById(adopt.getWardId());
        adoptDTO.setWardName(ward.getName());
        adoptDTO.setDistrictName(ward.getDistrict().getName());
        adoptDTO.setProvinceName(ward.getDistrict().getProvince().getName());
        return adoptDTO;
    }

    private void logCreate(Adopt adopt) {
        actionLogService.create(ActionLogDTO.builder()
                .action(Constants.ActionLog.CREATE)
                .description(Constants.ActionLog.CREATE + "." + TAG)
                .createdBy(adopt.getCreatedBy().getId())
                .details(List.of(
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(adopt.getId())
                                .columnName("code")
                                .oldValue("")
                                .newValue(adopt.getCode())
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(adopt.getId())
                                .columnName("pet_id")
                                .oldValue("")
                                .newValue(adopt.getPet().getId())
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(adopt.getId())
                                .columnName("ward_id")
                                .oldValue("")
                                .newValue(String.valueOf(adopt.getWardId()))
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(adopt.getId())
                                .columnName("district_id")
                                .oldValue("")
                                .newValue(String.valueOf(adopt.getDistrictId()))
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(adopt.getId())
                                .columnName("province_id")
                                .oldValue("")
                                .newValue(String.valueOf(adopt.getProvinceId()))
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(adopt.getId())
                                .columnName("address")
                                .oldValue("")
                                .newValue(adopt.getAddress().trim())
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(adopt.getId())
                                .columnName("reason")
                                .oldValue("")
                                .newValue(adopt.getReason().trim())
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(adopt.getId())
                                .columnName("status")
                                .oldValue("")
                                .newValue(String.valueOf(adopt.getStatus()))
                                .build()
                ))
                .build());
    }

    private void logUpdate(Adopt oldValue, AdoptUpdateRequest newValue) {
        List<ActionLogDetail> details = new ArrayList<>();
        if (!Objects.equals(oldValue.getWardId(), newValue.getWardId())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(newValue.getId())
                    .columnName("ward_id")
                    .oldValue(String.valueOf(oldValue.getWardId()))
                    .newValue(String.valueOf(newValue.getWardId()))
                    .build());
        }
        if (!Objects.equals(oldValue.getDistrictId(), newValue.getDistrictId())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(newValue.getId())
                    .columnName("district_id")
                    .oldValue(String.valueOf(oldValue.getDistrictId()))
                    .newValue(String.valueOf(newValue.getDistrictId()))
                    .build());
        }
        if (!Objects.equals(oldValue.getProvinceId(), newValue.getProvinceId())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(newValue.getId())
                    .columnName("province_id")
                    .oldValue(String.valueOf(oldValue.getProvinceId()))
                    .newValue(String.valueOf(newValue.getProvinceId()))
                    .build());
        }
        if (!Objects.equals(oldValue.getAddress(), newValue.getAddress().trim())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(newValue.getId())
                    .columnName("address")
                    .oldValue(oldValue.getAddress())
                    .newValue(newValue.getAddress().trim())
                    .build());
        }
        if (!Objects.equals(oldValue.getReason(), newValue.getReason().trim())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(newValue.getId())
                    .columnName("reason")
                    .oldValue(oldValue.getReason())
                    .newValue(newValue.getReason().trim())
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
