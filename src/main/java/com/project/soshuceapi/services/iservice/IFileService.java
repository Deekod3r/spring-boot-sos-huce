package com.project.soshuceapi.services.iservice;

import com.project.soshuceapi.models.requests.ImageCreateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface IFileService {

    Map<String, String> upload(MultipartFile multipartFile);

    void createImage(ImageCreateRequest request);

    void deleteImage(String id, String deletedBy);

}
