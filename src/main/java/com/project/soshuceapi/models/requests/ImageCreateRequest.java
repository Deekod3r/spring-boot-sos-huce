package com.project.soshuceapi.models.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.soshuceapi.common.ResponseMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImageCreateRequest {
    @NotBlank(message = ResponseMessage.Image.MISSING_OBJECT_NAME)
    String objectName;
    @NotBlank(message = ResponseMessage.Image.MISSING_OBJECT_ID)
    String objectId;
    @NotEmpty(message = ResponseMessage.Image.MISSING_IMAGES)
    List<MultipartFile> images;
    @JsonIgnore
    String createdBy;
}
