package com.project.soshuceapi.models.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.soshuceapi.common.ResponseMessage;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GalleriaUpdateImageRequest {
    @NotBlank(message = ResponseMessage.Galleria.MISSING_ID)
    String id;
    @NotBlank(message = ResponseMessage.Galleria.MISSING_IMAGE)
    MultipartFile image;
    @JsonIgnore
    String updatedBy;
}
