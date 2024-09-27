package com.project.soshuceapi.services;

import com.project.soshuceapi.common.Constants;
import com.project.soshuceapi.common.ResponseMessage;
import com.project.soshuceapi.entities.News;
import com.project.soshuceapi.entities.NewsCategory;
import com.project.soshuceapi.entities.logging.ActionLogDetail;
import com.project.soshuceapi.exceptions.BadRequestException;
import com.project.soshuceapi.models.DTOs.ActionLogDTO;
import com.project.soshuceapi.models.DTOs.NewsDTO;
import com.project.soshuceapi.models.requests.NewsCreateRequest;
import com.project.soshuceapi.models.requests.NewsSearchRequest;
import com.project.soshuceapi.models.requests.NewsUpdateImageRequest;
import com.project.soshuceapi.models.requests.NewsUpdateRequest;
import com.project.soshuceapi.repositories.NewsCategoryRepo;
import com.project.soshuceapi.repositories.NewsRepo;
import com.project.soshuceapi.services.iservice.IActionLogService;
import com.project.soshuceapi.services.iservice.IFileService;
import com.project.soshuceapi.services.iservice.INewsService;
import com.project.soshuceapi.utils.DataUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class NewsService implements INewsService {

    private static final String TAG = "NEWS";

    @Autowired
    private NewsRepo newsRepo;
    @Autowired
    private NewsCategoryRepo newsCategoryRepo;
    @Autowired
    private IFileService fileService;
    @Autowired
    private IActionLogService actionLogService;

    @Override
    public Map<String, Object> getAll(NewsSearchRequest request) {
        try {
            Page<Object[]> news = newsRepo.findAll(
                    request.getTitle(), request.getCategoryId(), request.getStatus(),
                    DataUtil.parseLocalDateTime(request.getFromDate()),
                    DataUtil.parseLocalDateTime(request.getToDate()),
                    Boolean.TRUE.equals(request.getFullData())
                            ? Pageable.unpaged()
                            : Pageable.ofSize(request.getLimit()).withPage(request.getPage() - 1)
            );
            List<NewsDTO> newsDTOs = new ArrayList<>();
            for (Object[] item : news.getContent()) {
                NewsDTO newsDTO = NewsDTO.builder()
                        .id(DataUtil.parseString(item[0]))
                        .title(DataUtil.parseString(item[1]))
                        .description(DataUtil.parseString(item[2]))
                        .image(DataUtil.parseString(item[3]))
                        .status(DataUtil.parseBoolean(item[4]))
                        .categoryId(DataUtil.parseString(item[5]))
                        .categoryName(DataUtil.parseString(item[6]))
                        .createdAt(DataUtil.parseLocalDateTime(item[7]))
                        .build();
                newsDTOs.add(newsDTO);
            }
            return Map.of(
                    "news", newsDTOs,
                    "totalElements", news.getTotalElements(),
                    "totalPages", news.getTotalPages(),
                    "currentPage", news.getNumber() + 1
            );
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public NewsDTO getById(String id) {
        try {
            return newsRepo.findById(id)
                    .map(this::parseNewsDTO)
                    .orElseThrow(() -> new BadRequestException(ResponseMessage.News.NOT_FOUND));
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void create(NewsCreateRequest request) {
        try {
            News news = newsRepo.findByTitle(request.getTitle().trim());
            if (Objects.nonNull(news)) {
                throw new BadRequestException(ResponseMessage.News.TITLE_EXISTED);
            }
            NewsCategory newsCategory = newsCategoryRepo.findById(request.getCategoryId())
                    .orElseThrow(() -> new BadRequestException(ResponseMessage.NewsCategory.NOT_FOUND));
            Map<String, String> data = fileService.upload(request.getImage());
            String url = data.get("url");
            news = new News();
            news.setTitle(request.getTitle().trim());
            news.setContent(request.getContent().trim());
            news.setDescription(request.getDescription().trim());
            news.setImage(url);
            news.setStatus(true);
            news.setNewsCategory(newsCategory);
            news.setCreatedAt(LocalDateTime.now());
            news.setCreatedBy(request.getCreatedBy());
            logCreate(newsRepo.save(news));
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void update(NewsUpdateRequest request) {
        try {
            News news = newsRepo.findById(request.getId())
                    .orElseThrow(() -> new BadRequestException(ResponseMessage.News.NOT_FOUND));
            News existedNews = newsRepo.findByTitle(request.getTitle().trim());
            if (Objects.nonNull(existedNews) && !Objects.equals(existedNews.getId(), request.getId())) {
                throw new BadRequestException(ResponseMessage.News.TITLE_EXISTED);
            }
            NewsCategory newsCategory = null;
            if (!Objects.equals(request.getCategoryId(), news.getNewsCategory().getId())) {
                newsCategory = newsCategoryRepo.findById(request.getCategoryId())
                        .orElseThrow(() -> new BadRequestException(ResponseMessage.NewsCategory.NOT_FOUND));
            }
            logUpdate(news, request);
            news.setTitle(request.getTitle().trim());
            news.setContent(request.getContent().trim());
            news.setDescription(request.getDescription().trim());
            news.setStatus(request.getStatus());
            if (Objects.nonNull(newsCategory)) {
                news.setNewsCategory(newsCategory);
            }
            news.setUpdatedAt(LocalDateTime.now());
            news.setUpdatedBy(request.getUpdatedBy());
            newsRepo.save(news);
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updateImage(NewsUpdateImageRequest request) {
        try {
            News news = newsRepo.findById(request.getId())
                    .orElseThrow(() -> new BadRequestException(ResponseMessage.News.NOT_FOUND));
            Map<String, String> data = fileService.upload(request.getImage());
            String url = data.get("url");
            actionLogService.create(ActionLogDTO.builder()
                    .action(Constants.ActionLog.UPDATE)
                    .description(Constants.ActionLog.UPDATE + "." + TAG)
                    .createdBy(request.getUpdatedBy())
                    .details(List.of(
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(news.getId())
                                    .columnName("image")
                                    .oldValue(news.getImage())
                                    .newValue(url)
                                    .build()
                    ))
                    .build());
            news.setImage(url);
            news.setUpdatedAt(LocalDateTime.now());
            news.setUpdatedBy(request.getUpdatedBy());
            newsRepo.save(news);
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
            News news = newsRepo.findById(id)
                    .orElseThrow(() -> new BadRequestException(ResponseMessage.News.NOT_FOUND));
            logDelete(news, deletedBy);
            newsRepo.delete(news);
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private NewsDTO parseNewsDTO(News news) {
        NewsDTO newsDTO = new NewsDTO();
        newsDTO.setId(news.getId());
        newsDTO.setTitle(news.getTitle());
        newsDTO.setContent(news.getContent());
        newsDTO.setDescription(news.getDescription());
        newsDTO.setImage(news.getImage());
        newsDTO.setStatus(news.getStatus());
        newsDTO.setCategoryId(news.getNewsCategory().getId());
        newsDTO.setCategoryName(news.getNewsCategory().getName());
        newsDTO.setCreatedAt(news.getCreatedAt());
        return newsDTO;
    }

    private void logCreate(News news) {
        actionLogService.create(ActionLogDTO.builder()
                .action(Constants.ActionLog.CREATE)
                .description(Constants.ActionLog.CREATE + "." + TAG)
                .createdBy(news.getCreatedBy())
                .details(List.of(
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(news.getId())
                                .columnName("title")
                                .oldValue("")
                                .newValue(news.getTitle().trim())
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(news.getId())
                                .columnName("content")
                                .oldValue("")
                                .newValue(news.getContent().trim())
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(news.getId())
                                .columnName("description")
                                .oldValue("")
                                .newValue(news.getDescription().trim())
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(news.getId())
                                .columnName("image")
                                .oldValue("")
                                .newValue(news.getImage())
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(news.getId())
                                .columnName("status")
                                .oldValue("")
                                .newValue(String.valueOf(news.getStatus()))
                                .build()
                ))
                .build());
    }

    private void logUpdate(News oldValue, NewsUpdateRequest newValue) {
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
        if (!Objects.equals(oldValue.getContent(), newValue.getContent().trim())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(oldValue.getId())
                    .columnName("content")
                    .oldValue(oldValue.getContent())
                    .newValue(newValue.getContent().trim())
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
                    .oldValue(String.valueOf(oldValue.getStatus()))
                    .newValue(String.valueOf(newValue.getStatus()))
                    .build());
        }
        if (!Objects.equals(oldValue.getNewsCategory().getId(), newValue.getCategoryId())) {
            details.add(ActionLogDetail.builder()
                    .tableName(TAG)
                    .rowId(oldValue.getId())
                    .columnName("category_id")
                    .oldValue(oldValue.getNewsCategory().getId())
                    .newValue(newValue.getCategoryId())
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

    private void logDelete(News news, String deletedBy) {
        actionLogService.create(ActionLogDTO.builder()
                .action(Constants.ActionLog.DELETE)
                .description(Constants.ActionLog.DELETE + "." + TAG)
                .createdBy(deletedBy)
                .details(List.of(
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(news.getId())
                                .columnName("status")
                                .oldValue(String.valueOf(news.getStatus()))
                                .newValue("")
                                .build()
                ))
                .build());
    }

}
