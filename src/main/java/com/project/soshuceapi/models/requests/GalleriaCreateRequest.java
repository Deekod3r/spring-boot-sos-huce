package com.project.soshuceapi.models.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.soshuceapi.common.ResponseMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GalleriaCreateRequest {
    @NotBlank(message = ResponseMessage.Galleria.MISSING_TITLE)
    @Length(min = 2, max = 100, message = ResponseMessage.Galleria.INVALID_TITLE)
    String title;
    @NotBlank(message = ResponseMessage.Galleria.MISSING_DESCRIPTION)
    @Length(min = 2, max = 255, message = ResponseMessage.Galleria.INVALID_DESCRIPTION)
    String description;
    @NotNull(message = ResponseMessage.Galleria.MISSING_IMAGE)
    MultipartFile image;
    @NotNull(message = ResponseMessage.Galleria.MISSING_LINK)
    String link;
    @JsonIgnore
    String createdBy;
}
