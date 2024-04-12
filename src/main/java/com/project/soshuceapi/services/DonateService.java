package com.project.soshuceapi.services;

import com.project.soshuceapi.common.Constants;
import com.project.soshuceapi.common.ResponseMessage;
import com.project.soshuceapi.entities.Donate;
import com.project.soshuceapi.entities.logging.ActionLogDetail;
import com.project.soshuceapi.exceptions.BadRequestException;
import com.project.soshuceapi.models.DTOs.ActionLogDTO;
import com.project.soshuceapi.models.DTOs.DonateDTO;
import com.project.soshuceapi.models.DTOs.TotalAmountStatisticDTO;
import com.project.soshuceapi.models.requests.DonateCreateRequest;
import com.project.soshuceapi.models.requests.DonateSearchRequest;
import com.project.soshuceapi.models.requests.DonateUpdateRequest;
import com.project.soshuceapi.models.requests.TotalDonateSearchRequest;
import com.project.soshuceapi.repositories.DonateRepo;
import com.project.soshuceapi.services.iservice.IActionLogService;
import com.project.soshuceapi.services.iservice.IDonateService;
import com.project.soshuceapi.utils.DataUtil;
import com.project.soshuceapi.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class DonateService implements IDonateService {

    private static final String TAG = "DONATE";

    @Autowired
    private DonateRepo donateRepo;
    @Autowired
    private IActionLogService actionLogService;

    @Override
    public Map<String, Object> getAll(DonateSearchRequest request) {
        try {
            Page<Donate> donates = donateRepo.findAll(
                    request.getRemitter(),
                    request.getPayee(),
                    request.getFromDate(),
                    request.getToDate(),
                    request.getFullData() ? Pageable.unpaged() : Pageable.ofSize(request.getLimit()).withPage(request.getPage() - 1)
            );
            return Map.of(
                    "donates", donates.getContent().stream()
                        .map(this::parseDonateDTO)
                        .toList(),
                    "totalElements", donates.getTotalElements(),
                    "totalPages", donates.getTotalPages(),
                    "currentPage", donates.getNumber() + 1
            );
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public DonateDTO getById(String id) {
        try {
            return donateRepo.findById(id)
                    .map(this::parseDonateDTO)
                    .orElseThrow(()-> new BadRequestException(ResponseMessage.Donate.NOT_FOUND));
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<TotalAmountStatisticDTO> getTotalDonation(TotalDonateSearchRequest request) {
        try {
            List<Object[]> data = donateRepo.calTotalDonation(request.getYear());
            return data.stream()
                    .map(d -> TotalAmountStatisticDTO.of(
                            DataUtil.parseInteger(d[0].toString()),
                            DataUtil.parseInteger(d[1].toString()),
                            DataUtil.parseBigDecimal(d[2].toString())
                    ))
                    .toList();
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void create(DonateCreateRequest request) {
        try {
            Donate donate = Donate.builder()
                    .remitter(request.getRemitter().trim())
                    .payee(request.getPayee().trim())
                    .type(request.getType())
                    .detail(!StringUtil.isNullOrBlank(request.getDetail()) ? request.getDetail().trim() : request.getDetail())
                    .amount(request.getAmount())
                    .date(request.getDate())
                    .isDeleted(false)
                    .createdAt(LocalDateTime.now())
                    .createdBy(request.getCreatedBy())
                    .build();
            logCreate(donateRepo.save(donate));
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void update(DonateUpdateRequest request) {
        try {
            Donate donate = donateRepo.findById(request.getId())
                    .orElseThrow(()-> new BadRequestException(ResponseMessage.Donate.NOT_FOUND));
            logUpdate(donate, request);
            donate.setRemitter(request.getRemitter().trim());
            donate.setPayee(request.getPayee().trim());
            donate.setDetail(!StringUtil.isNullOrBlank(request.getDetail()) ? request.getDetail().trim() : request.getDetail());
            donate.setAmount(request.getAmount());
            donate.setDate(request.getDate());
            donate.setUpdatedAt(LocalDateTime.now());
            donate.setUpdatedBy(request.getUpdatedBy());
            donateRepo.save(donate);
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
            Donate donate = donateRepo.findById(id)
                    .orElseThrow(()-> new BadRequestException(ResponseMessage.Donate.NOT_FOUND));
            actionLogService.create(ActionLogDTO.builder()
                    .action(Constants.ActionLog.DELETE_SOFT)
                    .description(Constants.ActionLog.DELETE_SOFT + "." + TAG)
                    .createdBy(deletedBy)
                    .details(List.of(
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(donate.getId())
                                    .columnName("is_deleted")
                                    .oldValue("false")
                                    .newValue("true")
                                    .build()
                    ))
                    .build());
            donate.setIsDeleted(true);
            donate.setDeletedAt(LocalDateTime.now());
            donate.setDeletedBy(deletedBy);
            donateRepo.save(donate);
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private DonateDTO parseDonateDTO(Donate donate) {
        return DonateDTO.of(
                donate.getId(),
                donate.getRemitter(),
                donate.getPayee(),
                donate.getType(),
                donate.getDetail(),
                donate.getAmount(),
                donate.getDate()
        );
    }

    private void logCreate(Donate donate) {
        actionLogService.create(ActionLogDTO.builder()
                        .action(Constants.ActionLog.CREATE)
                        .description(Constants.ActionLog.CREATE + "." + TAG)
                        .createdBy(donate.getCreatedBy())
                        .details(List.of(
                                ActionLogDetail.builder()
                                        .tableName(TAG)
                                        .rowId(donate.getId())
                                        .columnName("remitter")
                                        .oldValue("")
                                        .newValue(donate.getRemitter())
                                        .build(),
                                ActionLogDetail.builder()
                                        .tableName(TAG)
                                        .rowId(donate.getId())
                                        .columnName("payee")
                                        .oldValue("")
                                        .newValue(donate.getPayee())
                                        .build(),
                                ActionLogDetail.builder()
                                        .tableName(TAG)
                                        .rowId(donate.getId())
                                        .columnName("type")
                                        .oldValue("")
                                        .newValue(donate.getType().toString())
                                        .build(),
                                ActionLogDetail.builder()
                                        .tableName(TAG)
                                        .rowId(donate.getId())
                                        .columnName("detail")
                                        .oldValue("")
                                        .newValue(donate.getDetail())
                                        .build(),
                                ActionLogDetail.builder()
                                        .tableName(TAG)
                                        .rowId(donate.getId())
                                        .columnName("amount")
                                        .oldValue("")
                                        .newValue(donate.getAmount().toString())
                                        .build(),
                                ActionLogDetail.builder()
                                        .tableName(TAG)
                                        .rowId(donate.getId())
                                        .columnName("date")
                                        .oldValue("")
                                        .newValue(donate.getDate().toString())
                                        .build()
                        ))
                .build());
    }

    private void logUpdate(Donate oldValue, DonateUpdateRequest newValue) {
        List<ActionLogDetail> details = new ArrayList<>();
        if (!Objects.equals(oldValue.getRemitter(), newValue.getRemitter().trim())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(oldValue.getId())
                    .columnName("remitter")
                    .oldValue(oldValue.getRemitter())
                    .newValue(newValue.getRemitter().trim())
                    .build());
        }
        if (!Objects.equals(oldValue.getPayee(), newValue.getPayee().trim())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(oldValue.getId())
                    .columnName("payee")
                    .oldValue(oldValue.getPayee())
                    .newValue(newValue.getPayee().trim())
                    .build());
        }
        if (!Objects.equals(oldValue.getDetail(), newValue.getDetail().trim())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(oldValue.getId())
                    .columnName("detail")
                    .oldValue(!StringUtil.isNullOrBlank(oldValue.getDetail()) ? oldValue.getDetail() : "")
                    .newValue(!StringUtil.isNullOrBlank(newValue.getDetail()) ? newValue.getDetail().trim() : "")
                    .build());
        }
        if (!Objects.equals(oldValue.getAmount(), newValue.getAmount())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(oldValue.getId())
                    .columnName("amount")
                    .oldValue(oldValue.getAmount().toString())
                    .newValue(newValue.getAmount().toString())
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
