package com.project.soshuceapi.services.iservice;

import com.project.soshuceapi.models.DTOs.NewsDTO;
import com.project.soshuceapi.models.requests.NewsCreateRequest;
import com.project.soshuceapi.models.requests.NewsSearchRequest;
import com.project.soshuceapi.models.requests.NewsUpdateImageRequest;
import com.project.soshuceapi.models.requests.NewsUpdateRequest;

import java.util.Map;

public interface INewsService {

    Map<String, Object> getAll(NewsSearchRequest request);

    NewsDTO getById(String id);

    void create(NewsCreateRequest request);

    void update(NewsUpdateRequest request);

    void updateImage(NewsUpdateImageRequest request);

    void delete(String id, String deletedBy);

}
