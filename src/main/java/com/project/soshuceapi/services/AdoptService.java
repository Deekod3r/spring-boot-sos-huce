package com.project.soshuceapi.services;

import com.project.soshuceapi.common.Constants;
import com.project.soshuceapi.common.ResponseMessage;
import com.project.soshuceapi.entities.Adopt;
import com.project.soshuceapi.entities.User;
import com.project.soshuceapi.entities.locations.Ward;
import com.project.soshuceapi.entities.logging.ActionLogDetail;
import com.project.soshuceapi.exceptions.BadRequestException;
import com.project.soshuceapi.models.DTOs.ActionLogDTO;
import com.project.soshuceapi.models.DTOs.AdoptDTO;
import com.project.soshuceapi.models.DTOs.PetDTO;
import com.project.soshuceapi.models.mappers.AdoptMapper;
import com.project.soshuceapi.models.mappers.PetMapper;
import com.project.soshuceapi.models.requests.AdoptCreateRequest;
import com.project.soshuceapi.models.requests.AdoptSearchRequest;
import com.project.soshuceapi.models.requests.AdoptUpdateRequest;
import com.project.soshuceapi.models.requests.AdoptUpdateStatusRequest;
import com.project.soshuceapi.repositories.AdoptRepo;
import com.project.soshuceapi.services.iservice.*;
import com.project.soshuceapi.utils.DataUtil;
import com.project.soshuceapi.utils.StringUtil;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private PetMapper petMapper;
    @Autowired
    private AdoptMapper adoptMapper;

    @Override
    public Map<String, Object> getAll(AdoptSearchRequest request) {
        try {
            Page<Adopt> adopts = adoptRepo.findAll(
                    request.getStatus(), request.getCode(),
                    DataUtil.parseLocalDateTime(request.getFromDate()),
                    DataUtil.parseLocalDateTime(request.getToDate()),
                    request.getRegisteredBy(), request.getPetAdopt(),
                    Pageable.ofSize(request.getLimit()).withPage(request.getPage() - 1)
            );
            List<AdoptDTO> adoptDTOS = adopts.getContent().stream()
                    .map(this::parseAdoptDTO).toList();
            return Map.of(
                    "adopts", adoptDTOS,
                    "total", adopts.getTotalElements(),
                    "page", adopts.getNumber() + 1,
                    "limit", adopts.getSize(),
                    "totalPages", adopts.getTotalPages()
            );
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<AdoptDTO> getAllByUser(String userId) {
        try {
            List<Adopt> adopts = adoptRepo.findAllByUser(userId);
            return adopts.stream().map(this::parseAdoptDTO).toList();
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
                put("pet", petService.getById(adopt.getPet().getId()));
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
            PetDTO pet = petService.getById(request.getPetId());
            if (!Objects.equals(pet.getStatus(), Constants.PetStatus.WAIT_FOR_ADOPTING)) {
                throw new BadRequestException(ResponseMessage.Pet.NOT_AVAILABLE_FOR_ADOPT);
            }
            long count = adoptRepo.countByStatus(Constants.AdoptStatus.WAIT_FOR_PROGRESSING, request.getRegisteredBy());
            if (count >= 3) {
                throw new BadRequestException(ResponseMessage.Adopt.MAX_ADOPTS);
            }
            if (checkDuplicate(request.getPetId(), request.getRegisteredBy())) {
                throw new BadRequestException(ResponseMessage.Adopt.DUPLICATE_ADOPT);
            }
            Adopt adopt = new Adopt();
            adopt.setPet(petMapper.mapFrom(pet));
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
            adopt.setFee(0.0F);
            adopt = adoptRepo.save(adopt);
            logCreate(adopt);
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
            Adopt oldValue = adopt;
            if (!Objects.equals(adopt.getStatus(), Constants.AdoptStatus.WAIT_FOR_PROGRESSING)
            && !Objects.equals(adopt.getStatus(), Constants.AdoptStatus.IN_PROGRESS)) {
                throw new BadRequestException(ResponseMessage.Adopt.NOT_AVAILABLE_FOR_UPDATE);
            }
            adopt.setWardId(request.getWardId());
            adopt.setDistrictId(request.getDistrictId());
            adopt.setProvinceId(request.getProvinceId());
            adopt.setAddress(request.getAddress().trim());
            adopt.setReason(request.getReason().trim());
            adopt.setFee(request.getFee());
            adopt.setUpdatedAt(LocalDateTime.now());
            adopt.setUpdatedBy(request.getUpdatedBy());
            adopt = adoptRepo.save(adopt);
            logUpdate(adopt, oldValue);
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
                adopt.setStatus(Constants.AdoptStatus.CANCEL);
                adopt.setUpdatedAt(LocalDateTime.now());
                adoptRepo.save(adopt);
                actionLogService.create(ActionLogDTO.builder()
                        .action(Constants.ActionLog.UPDATE)
                        .description(Constants.ActionLog.UPDATE + "." + TAG)
                        .createdBy(userId)
                        .details(List.of(
                                ActionLogDetail.builder()
                                        .tableName(TAG)
                                        .rowId(adopt.getId())
                                        .columnName("status")
                                        .oldValue(String.valueOf(Constants.AdoptStatus.WAIT_FOR_PROGRESSING))
                                        .newValue(String.valueOf(Constants.AdoptStatus.CANCEL))
                                        .build()
                        ))
                        .build());
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
            Adopt oldValue = adopt;
            adopt.setStatus(request.getStatus());
            adopt.setUpdatedAt(LocalDateTime.now());
            adopt.setUpdatedBy(request.getUpdatedBy());
            if (Objects.equals(request.getStatus(), Constants.AdoptStatus.COMPLETE)) {
                adopt.setConfirmedBy(User.builder()
                        .id(request.getUpdatedBy())
                        .build());
                adopt.setConfirmedAt(LocalDateTime.now());
                petService.setAdoptedBy(adopt.getRegisteredBy().getId(), adopt.getPet().getId(), request.getUpdatedBy());
            }
            if (Objects.equals(request.getStatus(), Constants.AdoptStatus.REJECT)) {
                adopt.setRejectedBy(User.builder()
                        .id(request.getUpdatedBy())
                        .build());
                adopt.setRejectedAt(LocalDateTime.now());
                adopt.setRejectedReason(!StringUtil.isNullOrBlank(request.getMessage()) ? request.getMessage().trim() : request.getMessage());
            }
            adopt = adoptRepo.save(adopt);
            actionLogService.create(ActionLogDTO.builder()
                    .action(Constants.ActionLog.UPDATE)
                    .description(Constants.ActionLog.UPDATE + "." + TAG)
                    .createdBy(request.getUpdatedBy())
                    .details(List.of(
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(adopt.getId())
                                    .columnName("status")
                                    .oldValue(String.valueOf(oldValue.getStatus()))
                                    .newValue(String.valueOf(adopt.getStatus()))
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

    @Override
    @Transactional
    public void deleteSoft(String id, String deletedBy) {
        try {
            Adopt adopt = adoptRepo.findById(id).orElseThrow(
                    () -> new BadRequestException(ResponseMessage.Adopt.NOT_FOUND));
            adopt.setIsDeleted(true);
            adopt.setDeletedAt(LocalDateTime.now());
            adopt.setDeletedBy(deletedBy);
            adoptRepo.save(adopt);
            actionLogService.create(ActionLogDTO.builder()
                    .action(Constants.ActionLog.DELETE)
                    .description(Constants.ActionLog.DELETE + "." + TAG)
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
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Map<String, Long> statisticStatus(@Nullable String userId) {
        try {
            if (StringUtil.isNullOrBlank(userId)) {
                userId = "";
            }
            Long wait = adoptRepo.countByStatus(Constants.AdoptStatus.WAIT_FOR_PROGRESSING, userId);
            Long progress = adoptRepo.countByStatus(Constants.AdoptStatus.IN_PROGRESS, userId);
            Long reject = adoptRepo.countByStatus(Constants.AdoptStatus.REJECT, userId);
            Long cancel = adoptRepo.countByStatus(Constants.AdoptStatus.CANCEL, userId);
            Long complete = adoptRepo.countByStatus(Constants.AdoptStatus.COMPLETE, userId);
            Long total = adoptRepo.countByStatus(null, userId);
            return new HashMap<>() {{
                put("countWaiting", wait);
                put("countInProgress", progress);
                put("countReject", reject);
                put("countCancel", cancel);
                put("countComplete", complete);
                put("total", total);
            }};
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private Boolean checkDuplicate(String petId, String userId) {
        try {
            long count = adoptRepo.checkDuplicate(petId, userId);
            return count > 0;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private String generateCode() {
        try {
            Long seq = adoptRepo.getSEQ();
            return "ADT" + String.format("%05d", seq);
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    protected void logCreate(Adopt adopt) {
        try {
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
                                    .newValue(adopt.getAddress())
                                    .build(),
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(adopt.getId())
                                    .columnName("reason")
                                    .oldValue("")
                                    .newValue(String.valueOf(adopt.getReason()))
                                    .build(),
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(adopt.getId())
                                    .columnName("status")
                                    .oldValue("")
                                    .newValue(String.valueOf(adopt.getStatus()))
                                    .build(),
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(adopt.getId())
                                    .columnName("fee")
                                    .oldValue("")
                                    .newValue(String.valueOf(adopt.getFee()))
                                    .build()
                    ))
                    .build());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    protected void logUpdate(Adopt newValue, Adopt oldValue) {
        try {
            List<ActionLogDetail> actionLogDetails = new ArrayList<>();
            if (!Objects.equals(newValue.getWardId(), oldValue.getWardId())) {
                actionLogDetails.add(ActionLogDetail.builder()
                        .tableName(TAG)
                        .rowId(newValue.getId())
                        .columnName("ward_id")
                        .oldValue(String.valueOf(oldValue.getWardId()))
                        .newValue(String.valueOf(newValue.getWardId()))
                        .build());
            }
            if (!Objects.equals(newValue.getDistrictId(), oldValue.getDistrictId())) {
                actionLogDetails.add(ActionLogDetail.builder()
                        .tableName(TAG)
                        .rowId(newValue.getId())
                        .columnName("district_id")
                        .oldValue(String.valueOf(oldValue.getDistrictId()))
                        .newValue(String.valueOf(newValue.getDistrictId()))
                        .build());
            }
            if (!Objects.equals(newValue.getProvinceId(), oldValue.getProvinceId())) {
                actionLogDetails.add(ActionLogDetail.builder()
                        .tableName(TAG)
                        .rowId(newValue.getId())
                        .columnName("province_id")
                        .oldValue(String.valueOf(oldValue.getProvinceId()))
                        .newValue(String.valueOf(newValue.getProvinceId()))
                        .build());
            }
            if (!Objects.equals(newValue.getAddress(), oldValue.getAddress())) {
                actionLogDetails.add(ActionLogDetail.builder()
                        .tableName(TAG)
                        .rowId(newValue.getId())
                        .columnName("address")
                        .oldValue(oldValue.getAddress())
                        .newValue(newValue.getAddress())
                        .build());
            }
            if (!Objects.equals(newValue.getReason(), oldValue.getReason())) {
                actionLogDetails.add(ActionLogDetail.builder()
                        .tableName(TAG)
                        .rowId(newValue.getId())
                        .columnName("reason")
                        .oldValue(oldValue.getReason())
                        .newValue(newValue.getReason())
                        .build());
            }
            if (!Objects.equals(newValue.getFee(), oldValue.getFee())) {
                actionLogDetails.add(ActionLogDetail.builder()
                        .tableName(TAG)
                        .rowId(newValue.getId())
                        .columnName("fee")
                        .oldValue(String.valueOf(oldValue.getFee()))
                        .newValue(String.valueOf(newValue.getFee()))
                        .build());
            }
            actionLogService.create(ActionLogDTO.builder()
                    .action(Constants.ActionLog.UPDATE)
                    .description(Constants.ActionLog.UPDATE + "." + TAG)
                    .createdBy(newValue.getUpdatedBy())
                    .details(actionLogDetails)
                    .build());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private AdoptDTO parseAdoptDTO(Adopt adopt) {
        try {
            AdoptDTO adoptDTO = adoptMapper.mapTo(adopt, AdoptDTO.class);
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
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
