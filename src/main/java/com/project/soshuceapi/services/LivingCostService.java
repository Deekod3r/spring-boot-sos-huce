package com.project.soshuceapi.services;

import com.project.soshuceapi.common.Constants;
import com.project.soshuceapi.common.ResponseMessage;
import com.project.soshuceapi.entities.Image;
import com.project.soshuceapi.entities.LivingCost;
import com.project.soshuceapi.entities.logging.ActionLogDetail;
import com.project.soshuceapi.exceptions.BadRequestException;
import com.project.soshuceapi.models.DTOs.ActionLogDTO;
import com.project.soshuceapi.models.DTOs.LivingCostDTO;
import com.project.soshuceapi.models.DTOs.TotalAmountStatisticDTO;
import com.project.soshuceapi.models.requests.LivingCostCreateRequest;
import com.project.soshuceapi.models.requests.LivingCostSearchRequest;
import com.project.soshuceapi.models.requests.LivingCostUpdateRequest;
import com.project.soshuceapi.models.requests.TotalLivingCostSearchRequest;
import com.project.soshuceapi.repositories.ImageRepo;
import com.project.soshuceapi.repositories.LivingCostRepo;
import com.project.soshuceapi.services.iservice.IActionLogService;
import com.project.soshuceapi.services.iservice.IFileService;
import com.project.soshuceapi.services.iservice.ILivingCostService;
import com.project.soshuceapi.utils.DataUtil;
import com.project.soshuceapi.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class LivingCostService implements ILivingCostService {

    private final String TAG = "LIVING_COST";

    @Autowired
    private LivingCostRepo livingCostRepo;
    @Autowired
    private ImageRepo imageRepo;
    @Autowired
    private IFileService fileService;
    @Autowired
    private IActionLogService actionLogService;

    @Override
    public Map<String, Object> getAll(LivingCostSearchRequest request) {
        try {
            Page<LivingCost> livingCosts = livingCostRepo.findAll(
                    request.getFromDate(), request.getToDate(), request.getCategory(),
                    request.getFullData() ? Pageable.unpaged() : Pageable.ofSize(request.getLimit()).withPage(request.getPage() - 1));
            return Map.of(
                    "livingCosts", livingCosts.getContent().stream()
                            .map(livingCost -> parseLivingCostDTO(livingCost, false))
                            .toList(),
                    "totalElements", livingCosts.getTotalElements(),
                    "currentPage", livingCosts.getNumber() + 1,
                    "totalPages", livingCosts.getTotalPages()
            );
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public LivingCostDTO getById(String id) {
        try {
            return livingCostRepo.findById(id)
                    .map(livingCost -> parseLivingCostDTO(livingCost, true))
                    .orElseThrow(() -> new BadRequestException(ResponseMessage.LivingCost.NOT_FOUND));
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<TotalAmountStatisticDTO> getTotalLivingCost(TotalLivingCostSearchRequest request) {
        try {
            List<Object[]> data = livingCostRepo.calTotalLivingCost(request.getYear());
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
    public List<TotalAmountStatisticDTO> getTotalLivingCostByCategory(TotalLivingCostSearchRequest request) {
        try {
            List<Object[]> data = null;
            if (Objects.nonNull(request.getMonth())) {
                data = livingCostRepo.calTotalLivingCostByCategoryAndMonth(request.getYear(), request.getMonth());
                return data.stream()
                        .map(d -> TotalAmountStatisticDTO.builder()
                                .year(DataUtil.parseInteger(d[0].toString()))
                                .month(DataUtil.parseInteger(d[1].toString()))
                                .totalAmount(DataUtil.parseBigDecimal(d[2].toString()))
                                .category(DataUtil.parseInteger(d[3].toString()))
                                .build())
                        .toList();
            } else {
                data = livingCostRepo.calTotalLivingCostByCategory(request.getYear());
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
    public void create(LivingCostCreateRequest request) {
        try {
            LivingCost livingCost = new LivingCost();
            livingCost.setName(request.getName().trim());
            livingCost.setCost(request.getCost());
            livingCost.setDate(request.getDate());
            livingCost.setCategory(request.getCategory());
            livingCost.setNote(!StringUtil.isNullOrBlank(request.getNote()) ? request.getNote().trim() : request.getNote());
            livingCost.setIsDeleted(false);
            livingCost.setCreatedBy(request.getCreatedBy());
            livingCost.setCreatedAt(LocalDateTime.now());
            livingCost = livingCostRepo.save(livingCost);
            logCreate(livingCost);
            List<Image> images = new ArrayList<>();
            for(MultipartFile image : request.getImages()) {
                Map<String, String> data = fileService.upload(image);
                images.add(Image.builder()
                            .fileName(data.get("fileName"))
                            .fileUrl(data.get("url"))
                            .createdAt(LocalDateTime.now())
                            .createdBy(request.getCreatedBy())
                            .fileType(image.getContentType())
                            .objectId(livingCost.getId())
                            .objectName(livingCost.getName())
                            .build());
            }
            imageRepo.saveAll(images);
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void update(LivingCostUpdateRequest request) {
        try {
            LivingCost livingCost = livingCostRepo.findById(request.getId())
                    .orElseThrow(() -> new BadRequestException(ResponseMessage.LivingCost.NOT_FOUND));
            logUpdate(livingCost, request);
            livingCost.setName(request.getName().trim());
            livingCost.setCost(request.getCost());
            livingCost.setDate(request.getDate());
            livingCost.setCategory(request.getCategory());
            livingCost.setNote(!StringUtil.isNullOrBlank(request.getNote()) ? request.getNote().trim() : request.getNote());
            livingCost.setUpdatedBy(request.getUpdatedBy());
            livingCost.setUpdatedAt(LocalDateTime.now());
            livingCostRepo.save(livingCost);
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void delete(String id, String deletedBy) {
        try {
            LivingCost livingCost = livingCostRepo.findById(id)
                    .orElseThrow(() -> new BadRequestException(ResponseMessage.LivingCost.NOT_FOUND));
            livingCost.setIsDeleted(true);
            livingCost.setDeletedBy(deletedBy);
            livingCost.setDeletedAt(LocalDateTime.now());
            livingCostRepo.save(livingCost);
            actionLogService.create(ActionLogDTO.builder()
                    .action(Constants.ActionLog.DELETE)
                    .description(Constants.ActionLog.DELETE + "." + TAG)
                    .createdBy(deletedBy)
                    .details(List.of(
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(livingCost.getId())
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

    private LivingCostDTO parseLivingCostDTO(LivingCost livingCost, Boolean withImages) {
        return LivingCostDTO.builder()
                .id(livingCost.getId())
                .name(livingCost.getName())
                .category(livingCost.getCategory())
                .cost(livingCost.getCost())
                .date(livingCost.getDate())
                .note(livingCost.getNote())
                .images(withImages ? imageRepo.findByObjectId(livingCost.getId()) : null)
                .build();
    }

    private void logCreate(LivingCost livingCost) {
        actionLogService.create(ActionLogDTO.builder()
                .action(Constants.ActionLog.CREATE)
                .description(Constants.ActionLog.CREATE + "." + TAG)
                .createdBy(livingCost.getCreatedBy())
                .details(List.of(
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(livingCost.getId())
                                .columnName("name")
                                .oldValue("")
                                .newValue(livingCost.getName())
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(livingCost.getId())
                                .columnName("cost")
                                .oldValue("")
                                .newValue(livingCost.getCost().toString())
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(livingCost.getId())
                                .columnName("date")
                                .oldValue("")
                                .newValue(livingCost.getDate().toString())
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(livingCost.getId())
                                .columnName("category")
                                .oldValue("")
                                .newValue(livingCost.getCategory().toString())
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(livingCost.getId())
                                .columnName("note")
                                .oldValue("")
                                .newValue(!StringUtil.isNullOrBlank(livingCost.getNote()) ? livingCost.getNote().trim() : livingCost.getNote())
                                .build()
                ))
                .build());
    }

    private void logUpdate(LivingCost oldValue, LivingCostUpdateRequest newValue) {
        List<ActionLogDetail> details = new ArrayList<>();
        if (!Objects.equals(oldValue.getName(), newValue.getName().trim())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(oldValue.getId())
                    .columnName("name")
                    .oldValue(oldValue.getName())
                    .newValue(newValue.getName().trim())
                    .build());
        }
        if (!Objects.equals(oldValue.getCost(), newValue.getCost())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(oldValue.getId())
                    .columnName("cost")
                    .oldValue(oldValue.getCost().toString())
                    .newValue(newValue.getCost().toString())
                    .build());
        }
        if (!Objects.equals(oldValue.getDate(), newValue.getDate())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(oldValue.getId())
                    .columnName("date")
                    .oldValue(oldValue.getDate().toString())
                    .newValue(newValue.getDate().toString())
                    .build());
        }
        if (!Objects.equals(oldValue.getCategory(), newValue.getCategory())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(oldValue.getId())
                    .columnName("category")
                    .oldValue(oldValue.getCategory().toString())
                    .newValue(newValue.getCategory().toString())
                    .build());
        }
        if (!Objects.equals(oldValue.getNote(), !StringUtil.isNullOrBlank(newValue.getNote()) ? newValue.getNote().trim() : newValue.getNote())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(oldValue.getId())
                    .columnName("note")
                    .oldValue(oldValue.getNote())
                    .newValue(!StringUtil.isNullOrBlank(newValue.getNote()) ? newValue.getNote().trim() : newValue.getNote())
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
