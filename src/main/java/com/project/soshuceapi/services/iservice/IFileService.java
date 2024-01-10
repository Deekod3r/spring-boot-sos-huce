package com.project.soshuceapi.services.iservice;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public interface IFileService {

    Map<String, String> upload(MultipartFile multipartFile);

}
