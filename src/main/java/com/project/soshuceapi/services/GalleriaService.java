package com.project.soshuceapi.services;

import com.project.soshuceapi.common.Constants;
import com.project.soshuceapi.common.ResponseMessage;
import com.project.soshuceapi.entities.config.Galleria;
import com.project.soshuceapi.entities.logging.ActionLogDetail;
import com.project.soshuceapi.exceptions.BadRequestException;
import com.project.soshuceapi.models.DTOs.ActionLogDTO;
import com.project.soshuceapi.models.DTOs.GalleriaDTO;
import com.project.soshuceapi.models.requests.GalleriaCreateRequest;
import com.project.soshuceapi.models.requests.GalleriaUpdateImageRequest;
import com.project.soshuceapi.models.requests.GalleriaUpdateRequest;
import com.project.soshuceapi.repositories.GalleriaRepo;
import com.project.soshuceapi.services.iservice.IActionLogService;
import com.project.soshuceapi.services.iservice.IFileService;
import com.project.soshuceapi.services.iservice.IGalleriaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class GalleriaService implements IGalleriaService {

    private final String TAG = "GALLERIA";

    @Autowired
    private GalleriaRepo galleriaRepo;
    @Autowired
    private IFileService fileService;
    @Autowired
    private IActionLogService actionLogService;

    @Override
    public List<GalleriaDTO> getAll(Boolean status) {
        try {
            return galleriaRepo.findAll(status).stream()
                    .map(this::parseGalleriaDTO)
                    .toList();
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public GalleriaDTO getById(String id) {
        try {
            return galleriaRepo.findById(id).map(this::parseGalleriaDTO)
                    .orElseThrow(() -> new BadRequestException(ResponseMessage.Galleria.NOT_FOUND));
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void create(GalleriaCreateRequest request) {
        try {
            Map<String, String> data = fileService.upload(request.getImage());
            String image = data.get("url");
            long index = galleriaRepo.count();
            Galleria galleria = new Galleria();
            galleria.setTitle(request.getTitle().trim());
            galleria.setImage(image);
            galleria.setDescription(request.getDescription().trim());
            galleria.setStatus(true);
            galleria.setIndex((int) index + 1);
            galleria.setLink(request.getLink().trim());
            galleria.setCreatedBy(request.getCreatedBy());
            galleria.setCreatedAt(LocalDateTime.now());
            logCreate(galleriaRepo.save(galleria));
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void update(GalleriaUpdateRequest request) {
        try {
            Galleria galleria = galleriaRepo.findById(request.getId())
                    .orElseThrow(() -> new BadRequestException(ResponseMessage.Galleria.NOT_FOUND));
            logUpdate(galleria, request);
            galleria.setTitle(request.getTitle().trim());
            galleria.setDescription(request.getDescription().trim());
            galleria.setStatus(request.getStatus());
            galleria.setLink(request.getLink().trim());
            galleria.setIndex(request.getIndex());
            galleria.setUpdatedBy(request.getUpdatedBy());
            galleria.setUpdatedAt(LocalDateTime.now());
            galleriaRepo.save(galleria);
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updateImage(GalleriaUpdateImageRequest request) {
        try {
            Galleria galleria = galleriaRepo.findById(request.getId())
                    .orElseThrow(() -> new BadRequestException(ResponseMessage.Galleria.NOT_FOUND));
            Map<String, String> data = fileService.upload(request.getImage());
            String image = data.get("url");
            actionLogService.create(ActionLogDTO.builder()
                            .action(Constants.ActionLog.UPDATE)
                            .description(Constants.ActionLog.UPDATE + "." + TAG)
                            .createdBy(request.getUpdatedBy())
                            .details(List.of(
                                    ActionLogDetail.builder()
                                            .tableName(TAG)
                                            .rowId(galleria.getId())
                                            .columnName("image")
                                            .oldValue(galleria.getImage())
                                            .newValue(image)
                                            .build()
                            ))
                    .build());
            galleria.setImage(image);
            galleria.setUpdatedBy(request.getUpdatedBy());
            galleria.setUpdatedAt(LocalDateTime.now());
            galleriaRepo.save(galleria);
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
            Galleria galleria = galleriaRepo.findById(id)
                    .orElseThrow(() -> new BadRequestException(ResponseMessage.Galleria.NOT_FOUND));
            actionLogService.create(ActionLogDTO.builder()
                    .action(Constants.ActionLog.DELETE)
                    .description(Constants.ActionLog.DELETE + "." + TAG)
                    .createdBy(deletedBy)
                    .details(List.of(
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(galleria.getId())
                                    .columnName("title")
                                    .oldValue(galleria.getTitle())
                                    .newValue("")
                                    .build(),
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(galleria.getId())
                                    .columnName("description")
                                    .oldValue(galleria.getDescription())
                                    .newValue("")
                                    .build(),
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(galleria.getId())
                                    .columnName("status")
                                    .oldValue(galleria.getStatus().toString())
                                    .newValue("")
                                    .build(),
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(galleria.getId())
                                    .columnName("link")
                                    .oldValue(galleria.getLink())
                                    .newValue("")
                                    .build(),
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(galleria.getId())
                                    .columnName("image")
                                    .oldValue(galleria.getImage())
                                    .newValue("")
                                    .build()
                    ))
                    .build());
            galleriaRepo.delete(galleria);
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private GalleriaDTO parseGalleriaDTO (Galleria entity) {
        GalleriaDTO dto = new GalleriaDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setLink(entity.getLink());
        dto.setDescription(entity.getDescription());
        dto.setImage(entity.getImage());
        dto.setStatus(entity.getStatus());
        dto.setIndex(entity.getIndex());
        return dto;
    }

    private void logCreate(Galleria galleria) {
        actionLogService.create(ActionLogDTO.builder()
                .action(Constants.ActionLog.CREATE)
                .description(Constants.ActionLog.CREATE + "." + TAG)
                .createdBy(galleria.getCreatedBy())
                .details(List.of(
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(galleria.getId())
                                .columnName("title")
                                .oldValue("")
                                .newValue(galleria.getTitle().trim())
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(galleria.getId())
                                .columnName("description")
                                .oldValue("")
                                .newValue(galleria.getDescription().trim())
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(galleria.getId())
                                .columnName("status")
                                .oldValue("")
                                .newValue(galleria.getStatus().toString())
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(galleria.getId())
                                .columnName("link")
                                .oldValue("")
                                .newValue(galleria.getLink())
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(galleria.getId())
                                .columnName("image")
                                .oldValue("")
                                .newValue(galleria.getImage())
                                .build()
                ))
                .build());
    }

    private void logUpdate(Galleria oldValue, GalleriaUpdateRequest newValue) {
        List<ActionLogDetail> details = new ArrayList<>();
        if (!Objects.equals(oldValue.getTitle(), newValue.getTitle().trim())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(oldValue.getId())
                    .columnName("title")
                    .oldValue(oldValue.getTitle())
                    .newValue(newValue.getTitle().trim())
                    .build());
        }
        if (!Objects.equals(oldValue.getDescription(), newValue.getDescription().trim())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(oldValue.getId())
                    .columnName("description")
                    .oldValue(oldValue.getDescription())
                    .newValue(newValue.getDescription().trim())
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
        if (!Objects.equals(oldValue.getLink(), newValue.getLink().trim())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(oldValue.getId())
                    .columnName("link")
                    .oldValue(oldValue.getLink())
                    .newValue(newValue.getLink().trim())
                    .build());
        }
        if (!Objects.equals(oldValue.getIndex(), newValue.getIndex())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(oldValue.getId())
                    .columnName("status")
                    .oldValue(String.valueOf(oldValue.getIndex()))
                    .newValue(String.valueOf(newValue.getIndex()))
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
