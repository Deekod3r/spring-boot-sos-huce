package com.project.soshuceapi.services;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.project.soshuceapi.config.ResourceConfig;
import com.project.soshuceapi.services.iservice.IFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class FileService implements IFileService {

    private final static String TAG = "FILE";

    @Autowired
    private ResourceConfig resourceConfig;

    @Override
    public Map<String, String> upload(MultipartFile multipartFile) {
        try {
            String fileName = multipartFile.getOriginalFilename();
            assert fileName != null;
            fileName = UUID.randomUUID().toString().concat(this.getExtension(fileName));
            File file = this.convertToFile(multipartFile, fileName);
            String URL = this.uploadFile(file, fileName);
            if(!file.delete()) {
                log.error(TAG + ": error.delete.file");
            }
            return Map.of("url", URL, "fileName", fileName);
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
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
            String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media";
            return String.format(DOWNLOAD_URL,
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
            throw new RuntimeException(e.getMessage());
        }
    }

}
