package com.project.soshuceapi.controllers;

import com.project.soshuceapi.common.ResponseMessage;
import com.project.soshuceapi.models.DTOs.FileDTO;
import com.project.soshuceapi.models.responses.Response;
import com.project.soshuceapi.services.iservice.IFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        log.info("HIT -/upload | File Name : {}", multipartFile.getOriginalFilename());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Response<FileDTO> response = new Response<>();
        response.setSuccess(false);
        try {
            Map<String, String> data = fileService.upload(multipartFile);
            FileDTO fileDTO = FileDTO.builder()
                            .name(data.get("fileName"))
                            .url(data.get("url"))
                            .build();
            response.setData(fileDTO);
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage(ResponseMessage.Common.SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/upload-multiple")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String uploadMultipleFiles() {
        return "upload.multiple.files";
    }

    @PostMapping("/download")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String downloadFile() {
        return "download.file";
    }

    @PostMapping("/download-multiple")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String downloadMultipleFiles() {
        return "download.multiple.files";
    }

}
