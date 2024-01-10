package com.project.soshuceapi.controllers;

import com.project.soshuceapi.common.ResponseCode;
import com.project.soshuceapi.entities.Image;
import com.project.soshuceapi.models.responses.Error;
import com.project.soshuceapi.models.responses.Response;
import com.project.soshuceapi.services.iservice.IFileService;
import com.project.soshuceapi.services.iservice.IImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    private IFileService fileService;
    @Autowired
    private IImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        log.info("HIT -/upload | File Name : {}", multipartFile.getOriginalFilename());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Response<Image> response = new Response<>();
        try {
            Map<String, String> data = fileService.upload(multipartFile);
            Image image = Image.builder()
                            .name(data.get("fileName"))
                            .url(data.get("url"))
                            .build();
            response.setData(image);
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setError(Error.of(e.getMessage(), ResponseCode.Common.FAIL));
            response.setSuccess(false);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/upload-multiple")
    public String uploadMultipleFiles() {
        return "upload multiple files";
    }

    @PostMapping("/download")
    public String downloadFile() {
        return "download file";
    }

    @PostMapping("/download-multiple")
    public String downloadMultipleFiles() {
        return "download multiple files";
    }

}
