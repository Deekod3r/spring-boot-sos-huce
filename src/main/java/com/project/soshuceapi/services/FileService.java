package com.project.soshuceapi.services;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.project.soshuceapi.common.Constants;
import com.project.soshuceapi.common.ResponseMessage;
import com.project.soshuceapi.config.ResourceConfig;
import com.project.soshuceapi.entities.Image;
import com.project.soshuceapi.entities.logging.ActionLogDetail;
import com.project.soshuceapi.exceptions.BadRequestException;
import com.project.soshuceapi.models.DTOs.ActionLogDTO;
import com.project.soshuceapi.models.requests.ImageCreateRequest;
import com.project.soshuceapi.repositories.ImageRepo;
import com.project.soshuceapi.services.iservice.IActionLogService;
import com.project.soshuceapi.services.iservice.IFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class FileService implements IFileService {

    private static final String TAG = "FILE";

    @Autowired
    private ResourceConfig resourceConfig;
    @Autowired
    private ImageRepo imageRepo;
    @Autowired
    private IActionLogService actionLogService;

    @Override
    public Map<String, String> upload(MultipartFile multipartFile) {
        try {
            String fileName = multipartFile.getOriginalFilename();
            assert fileName != null;
            fileName = UUID.randomUUID().toString().concat(this.getExtension(fileName));
            File file = this.convertToFile(multipartFile, fileName);
            String url = this.uploadFile(file, fileName);
            if (!file.delete()) {
                log.error(TAG + ": error.delete.file");
            }
            return Map.of("url", url, "fileName", fileName);
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void createImage(ImageCreateRequest request) {
        try {
            List<Image> images = new ArrayList<>();
            for(MultipartFile image : request.getImages()) {
                Map<String, String> data = upload(image);
                images.add(Image.builder()
                        .fileName(data.get("fileName"))
                        .fileUrl(data.get("url"))
                        .createdAt(LocalDateTime.now())
                        .createdBy(request.getCreatedBy())
                        .fileType(image.getContentType())
                        .objectId(request.getObjectId())
                        .objectName(request.getObjectName())
                        .build());
            }
            imageRepo.saveAll(images);
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteImage(String id, String deletedBy) {
        try {
            Image image = imageRepo.findById(id).orElseThrow(
                    () -> new BadRequestException(ResponseMessage.Image.NOT_FOUND));
            actionLogService.create(ActionLogDTO.builder()
                    .action(Constants.ActionLog.DELETE)
                    .description(Constants.ActionLog.DELETE + "." + TAG)
                    .createdBy(deletedBy)
                    .details(List.of(
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(id)
                                    .columnName("image")
                                    .oldValue(image.getObjectName() + "/" + image.getFileName())
                                    .newValue("")
                                    .build()
                    ))
            .build());
            imageRepo.delete(image);
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private String uploadFile(File file, String fileName) throws IOException {
        try {
            BlobId blobId = BlobId.of(resourceConfig.getFirebaseBucketName(), fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("media").build();
            InputStream inputStream = FileService.class.getClassLoader().getResourceAsStream("static/firebase-private-key.json");
            assert inputStream != null;
            Credentials credentials = GoogleCredentials.fromStream(inputStream);
            Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
            storage.create(blobInfo, Files.readAllBytes(file.toPath()));
            String downloadUrl = "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media";
            return String.format(downloadUrl,
                    URLEncoder.encode(resourceConfig.getFirebaseBucketName(), StandardCharsets.UTF_8),
                    URLEncoder.encode(fileName, StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new IOException(e.getMessage());
        }
    }

    private File convertToFile(MultipartFile multipartFile, String fileName) throws IOException {
        File tempFile = new File(fileName);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new IOException(e.getMessage());
        }
        return tempFile;
    }

    private String getExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

}
