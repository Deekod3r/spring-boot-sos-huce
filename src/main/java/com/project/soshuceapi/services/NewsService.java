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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NewsService implements INewsService {

    private final String TAG = "NEWS";

    @Autowired
    private NewsRepo newsRepo;
    @Autowired
    private NewsCategoryRepo newsCategoryRepo;
    @Autowired
    private IFileService fileService;
    @Autowired
    private IActionLogService actionLogService;

    @Override
    public List<NewsDTO> getAll(NewsSearchRequest request) {
        try {
            List<News> news = newsRepo.findAll(
                    request.getTitle(), request.getCategoryId(), request.getStatus(),
                    DataUtil.parseLocalDateTime(request.getFromDate()),
                    DataUtil.parseLocalDateTime(request.getToDate())
            );
            return news.stream().map(this::parseNewsDTO).collect(Collectors.toList());
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
            News news = newsRepo.findByTitle(request.getTitle());
            if(Objects.nonNull(news)) {
                throw new BadRequestException(ResponseMessage.News.TITLE_EXISTED);
            }
            NewsCategory newsCategory = newsCategoryRepo.findById(request.getCategoryId())
                    .orElseThrow(() -> new BadRequestException(ResponseMessage.NewsCategory.NOT_FOUND));
            Map<String, String> data = fileService.upload(request.getImage());
            String url = data.get("url");
            news = new News();
            news.setTitle(request.getTitle().trim());
            news.setContent(request.getContent().trim());
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
            News existedNews = newsRepo.findByTitle(request.getTitle());
            if (Objects.nonNull(existedNews) && !Objects.equals(existedNews.getId(), request.getId())) {
                throw new BadRequestException(ResponseMessage.News.TITLE_EXISTED);
            }
            NewsCategory newsCategory = null;
            if(!Objects.equals(request.getCategoryId(), news.getNewsCategory().getId())) {
                newsCategory = newsCategoryRepo.findById(request.getCategoryId())
                        .orElseThrow(() -> new BadRequestException(ResponseMessage.NewsCategory.NOT_FOUND));
            }
            logUpdate(news, request);
            news.setTitle(request.getTitle().trim());
            news.setContent(request.getContent().trim());
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
                                .newValue(news.getTitle())
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(news.getId())
                                .columnName("content")
                                .oldValue("")
                                .newValue(news.getContent())
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
        actionLogService.create(ActionLogDTO.builder()
                .action(Constants.ActionLog.UPDATE)
                .description(Constants.ActionLog.UPDATE + "." + TAG)
                .createdBy(newValue.getUpdatedBy())
                .details(List.of(
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(oldValue.getId())
                                .columnName("title")
                                .oldValue(oldValue.getTitle())
                                .newValue(newValue.getTitle().trim())
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(oldValue.getId())
                                .columnName("content")
                                .oldValue(oldValue.getContent())
                                .newValue(newValue.getContent().trim())
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(oldValue.getId())
                                .columnName("status")
                                .oldValue(String.valueOf(oldValue.getStatus()))
                                .newValue(String.valueOf(newValue.getStatus()))
                                .build()
                ))
                .build());
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
                                .columnName("title")
                                .oldValue(news.getTitle())
                                .newValue("")
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(news.getId())
                                .columnName("content")
                                .oldValue(news.getContent())
                                .newValue("")
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .rowId(news.getId())
                                .columnName("image")
                                .oldValue(news.getImage())
                                .newValue("")
                                .build(),
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
