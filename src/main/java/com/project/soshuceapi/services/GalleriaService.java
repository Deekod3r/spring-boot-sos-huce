package com.project.soshuceapi.services;

import com.project.soshuceapi.common.Constants;
import com.project.soshuceapi.entities.config.Galleria;
import com.project.soshuceapi.entities.logging.ActionLogDetail;
import com.project.soshuceapi.models.DTOs.ActionLogDTO;
import com.project.soshuceapi.models.DTOs.GalleriaDTO;
import com.project.soshuceapi.models.requests.GalleriaCreateRequest;
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
import java.util.List;
import java.util.Map;

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
            return galleriaRepo.findAll(status).stream().map(entity -> {
                        GalleriaDTO dto = new GalleriaDTO();
                        dto.setId(entity.getId());
                        dto.setTitle(entity.getTitle());
                        dto.setLink(entity.getLink());
                        dto.setDescription(entity.getDescription());
                        dto.setImage(entity.getImage());
                        dto.setStatus(entity.getStatus());
                        dto.setIndex(entity.getIndex());
                        return dto;
                    })
                    .toList();
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public GalleriaDTO getById(String id) {
        log.info("Find Galleria By ID");
        return null;
    }

    @Override
    @Transactional
    public void create(GalleriaCreateRequest request) {
        try {
            Map<String, String> data = fileService.upload(request.getImage());
            String image = data.get("url");
            long index = galleriaRepo.countByStatus(true);
            Galleria galleria = new Galleria();
            galleria.setTitle(request.getTitle().trim());
            galleria.setImage(image);
            galleria.setDescription(request.getDescription().trim());
            galleria.setStatus(request.getStatus());
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
        log.info("Update Galleria");
    }

    @Override
    @Transactional
    public void delete(String id) {
        log.info("Delete Galleria");
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

}
