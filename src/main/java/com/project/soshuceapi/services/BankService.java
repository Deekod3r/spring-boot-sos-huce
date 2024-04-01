package com.project.soshuceapi.services;

import com.project.soshuceapi.common.Constants;
import com.project.soshuceapi.common.ResponseMessage;
import com.project.soshuceapi.entities.Bank;
import com.project.soshuceapi.entities.logging.ActionLogDetail;
import com.project.soshuceapi.exceptions.BadRequestException;
import com.project.soshuceapi.models.DTOs.ActionLogDTO;
import com.project.soshuceapi.models.DTOs.BankDTO;
import com.project.soshuceapi.models.requests.BankCreateRequest;
import com.project.soshuceapi.models.requests.BankUpdateRequest;
import com.project.soshuceapi.repositories.BankRepo;
import com.project.soshuceapi.services.iservice.IActionLogService;
import com.project.soshuceapi.services.iservice.IBankService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class BankService implements IBankService {

    private final static String TAG = "BANK";

    @Autowired
    private BankRepo bankRepo;
    @Autowired
    private IActionLogService actionLogService;

    @Override
    public List<BankDTO> getAll() {
        try {
            return bankRepo.findAll().stream().map(this::parseBankDTO).toList();
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public BankDTO getById(String id) {
        try {
            Bank bank = bankRepo.findById(id).orElseThrow(
                    () -> new BadRequestException(ResponseMessage.Bank.NOT_FOUND));
            return parseBankDTO(bank);
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void create(BankCreateRequest request) {
        try {
            if(bankRepo.count() >= Constants.Bank.MAX_QUANTITY) {
                throw new BadRequestException(ResponseMessage.Bank.MAX_QUANTITY);
            }
            if(Objects.nonNull(bankRepo.findByAccountNumber(request.getAccountNumber().trim()))) {
                throw new BadRequestException(ResponseMessage.Bank.NAME_EXISTED);
            }
            logCreate(bankRepo.save(
                    Bank.builder()
                            .name(request.getName().trim())
                            .accountNumber(request.getAccountNumber().trim())
                            .owner(request.getOwner().trim())
                            .logo(request.getLogo().trim())
                            .isDeleted(false)
                            .createdBy(request.getCreatedBy())
                            .createdAt(LocalDateTime.now())
                            .build()));
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void update(BankUpdateRequest request) {
        try {
            Bank bank = bankRepo.findById(request.getId()).orElseThrow(
                    () -> new BadRequestException(ResponseMessage.Bank.NOT_FOUND));
            if(Objects.nonNull(bankRepo.findByAccountNumber(request.getAccountNumber().trim()))
                    && !Objects.equals(bank.getAccountNumber(), request.getAccountNumber().trim())) {
                throw new BadRequestException(ResponseMessage.Bank.NAME_EXISTED);
            }
            logUpdate(bank, request);
            bank.setName(request.getName().trim());
            bank.setAccountNumber(request.getAccountNumber().trim());
            bank.setOwner(request.getOwner().trim());
            bank.setLogo(request.getLogo().trim());
            bank.setUpdatedBy(request.getUpdatedBy());
            bank.setUpdatedAt(LocalDateTime.now());
            bankRepo.save(bank);
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
            Bank bank = bankRepo.findById(id).orElseThrow(
                    () -> new BadRequestException(ResponseMessage.Bank.NOT_FOUND));
            actionLogService.create(
                    ActionLogDTO.builder()
                            .action(Constants.ActionLog.DELETE_SOFT)
                            .description(Constants.ActionLog.DELETE_SOFT + "." + TAG)
                            .createdBy(deletedBy)
                            .details(List.of(
                                    ActionLogDetail.builder()
                                            .tableName(TAG)
                                            .rowId(bank.getId())
                                            .columnName("is_deleted")
                                            .oldValue(String.valueOf(bank.getIsDeleted()))
                                            .newValue(String.valueOf(true))
                                            .build()
                            ))
                            .build());
            bank.setIsDeleted(true);
            bank.setDeletedBy(deletedBy);
            bank.setDeletedAt(LocalDateTime.now());
            bankRepo.save(bank);
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private BankDTO parseBankDTO(Bank bank) {
        return BankDTO.of(
                bank.getId(),
                bank.getName(),
                bank.getAccountNumber(),
                bank.getOwner(),
                bank.getLogo()
        );
    }

    private void logCreate(Bank bank) {
        actionLogService.create(
            ActionLogDTO.builder()
                    .action(Constants.ActionLog.CREATE)
                    .description(Constants.ActionLog.CREATE + " " + TAG)
                    .createdBy(bank.getCreatedBy())
                    .details(List.of(
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(bank.getId())
                                    .columnName("name")
                                    .oldValue("")
                                    .newValue(bank.getName().trim())
                                    .build(),
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(bank.getId())
                                    .columnName("account_number")
                                    .oldValue("")
                                    .newValue(bank.getAccountNumber().trim())
                                    .build(),
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(bank.getId())
                                    .columnName("owner")
                                    .oldValue("")
                                    .newValue(bank.getOwner().trim())
                                    .build(),
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(bank.getId())
                                    .columnName("logo")
                                    .oldValue("")
                                    .newValue(bank.getLogo().trim())
                                    .build()
                    ))
                    .build());
    }

    private void logUpdate(Bank oldValue, BankUpdateRequest newValue) {
        List<ActionLogDetail> details = new ArrayList<>();
        if(!Objects.equals(oldValue.getName(), newValue.getName().trim())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(oldValue.getId())
                    .columnName("name")
                    .oldValue(oldValue.getName())
                    .newValue(newValue.getName().trim())
                    .build());
        }
        if(!Objects.equals(oldValue.getAccountNumber(), newValue.getAccountNumber().trim())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(oldValue.getId())
                    .columnName("account_number")
                    .oldValue(oldValue.getAccountNumber())
                    .newValue(newValue.getAccountNumber().trim())
                    .build());
        }
        if(!Objects.equals(oldValue.getOwner(), newValue.getOwner().trim())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(oldValue.getId())
                    .columnName("owner")
                    .oldValue(oldValue.getOwner())
                    .newValue(newValue.getOwner().trim())
                    .build());
        }
        if(!Objects.equals(oldValue.getLogo(), newValue.getLogo().trim())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(oldValue.getId())
                    .columnName("logo")
                    .oldValue(oldValue.getLogo())
                    .newValue(newValue.getLogo().trim())
                    .build());
        }
        if(!details.isEmpty()) {
            actionLogService.create(
                    ActionLogDTO.builder()
                            .action(Constants.ActionLog.UPDATE)
                            .description(Constants.ActionLog.UPDATE + "." + TAG)
                            .createdBy(newValue.getUpdatedBy())
                            .details(details)
                            .build());
        }
    }

}
