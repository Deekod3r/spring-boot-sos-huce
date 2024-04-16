package com.project.soshuceapi.services;

import com.project.soshuceapi.common.Constants;
import com.project.soshuceapi.common.ResponseMessage;
import com.project.soshuceapi.entities.NewsCategory;
import com.project.soshuceapi.entities.logging.ActionLogDetail;
import com.project.soshuceapi.exceptions.BadRequestException;
import com.project.soshuceapi.models.DTOs.ActionLogDTO;
import com.project.soshuceapi.models.DTOs.NewsCategoryDTO;
import com.project.soshuceapi.models.requests.NewsCategoryCreateRequest;
import com.project.soshuceapi.models.requests.NewsCategoryUpdateRequest;
import com.project.soshuceapi.repositories.NewsCategoryRepo;
import com.project.soshuceapi.repositories.NewsRepo;
import com.project.soshuceapi.services.iservice.IActionLogService;
import com.project.soshuceapi.services.iservice.INewsCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NewsCategoryService implements INewsCategoryService {

    private final String TAG = "NEWS_CATEGORY";

    @Autowired
    private NewsCategoryRepo newsCategoryRepo;
    @Autowired
    private NewsRepo newsRepo;
    @Autowired
    private IActionLogService actionLogService;


    @Override
    public List<NewsCategoryDTO> getAll() {
        try {
            List<NewsCategory> newsCategories = newsCategoryRepo.findAll();
            return newsCategories.stream().map(this::parseNewsCategoryDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public NewsCategoryDTO getById(String id) {
        try {
            return newsCategoryRepo.findById(id)
                    .map(this::parseNewsCategoryDTO)
                    .orElseThrow(() -> new BadRequestException(ResponseMessage.NewsCategory.NOT_FOUND));
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void create(NewsCategoryCreateRequest request) {
        try {
            NewsCategory newsCategory = newsCategoryRepo.findByName(request.getName().trim());
            if (Objects.nonNull(newsCategory)) {
                throw new BadRequestException(ResponseMessage.NewsCategory.NAME_EXISTED);
            }
            newsCategory = new NewsCategory();
            newsCategory.setName(request.getName().trim());
            newsCategory.setDescription(request.getDescription().trim());
            newsCategory.setCreatedAt(LocalDateTime.now());
            newsCategory.setCreatedBy(request.getCreatedBy());
            logCreate(newsCategoryRepo.save(newsCategory));
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void update(NewsCategoryUpdateRequest request) {
        try {
            NewsCategory newsCategory = newsCategoryRepo.findById(request.getId())
                    .orElseThrow(() -> new BadRequestException(ResponseMessage.NewsCategory.NOT_FOUND));
            NewsCategory existedNewsCategory = newsCategoryRepo.findByName(request.getName().trim());
            if (Objects.nonNull(existedNewsCategory) && !Objects.equals(existedNewsCategory.getId(), request.getId())) {
                throw new BadRequestException(ResponseMessage.NewsCategory.NAME_EXISTED);
            }
            logUpdate(newsCategory, request);
            newsCategory.setName(request.getName().trim());
            newsCategory.setDescription(request.getDescription().trim());
            newsCategory.setUpdatedAt(LocalDateTime.now());
            newsCategory.setUpdatedBy(request.getUpdatedBy());
            newsCategoryRepo.save(newsCategory);
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
            NewsCategory newsCategory = newsCategoryRepo.findById(id)
                    .orElseThrow(() -> new BadRequestException(ResponseMessage.NewsCategory.NOT_FOUND));
            actionLogService.create(ActionLogDTO.builder()
                    .action(Constants.ActionLog.DELETE)
                    .description(Constants.ActionLog.DELETE + "." + TAG)
                    .createdBy(deletedBy)
                    .details(List.of(
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(newsCategory.getId())
                                    .columnName("name")
                                    .oldValue(newsCategory.getName())
                                    .newValue("")
                                    .build(),
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(newsCategory.getId())
                                    .columnName("description")
                                    .oldValue(newsCategory.getDescription())
                                    .newValue("")
                                    .build()
                    ))
                    .build());
            newsCategoryRepo.delete(newsCategory);
            newsRepo.deleteByNewsCategory(id);
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private NewsCategoryDTO parseNewsCategoryDTO(NewsCategory newsCategory) {
        return NewsCategoryDTO.builder()
                .id(newsCategory.getId())
                .name(newsCategory.getName())
                .description(newsCategory.getDescription())
                .createdAt(newsCategory.getCreatedAt())
                .build();
    }

    private void logCreate(NewsCategory newsCategory) {
        actionLogService.create(ActionLogDTO.builder()
                .action(Constants.ActionLog.CREATE)
                .description(Constants.ActionLog.CREATE + "." + TAG)
                .createdBy(newsCategory.getCreatedBy())
                .details(List.of(
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(newsCategory.getId())
                                .columnName("name")
                                .oldValue("")
                                .newValue(newsCategory.getName())
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(newsCategory.getId())
                                .columnName("description")
                                .oldValue("")
                                .newValue(newsCategory.getDescription())
                                .build()
                ))
                .build());
    }

    private void logUpdate(NewsCategory oldValue, NewsCategoryUpdateRequest newValue) {
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
        if (!Objects.equals(oldValue.getDescription(), newValue.getDescription().trim())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(oldValue.getId())
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
