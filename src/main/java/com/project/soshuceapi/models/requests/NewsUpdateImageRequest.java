package com.project.soshuceapi.models.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.soshuceapi.common.ResponseMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewsUpdateImageRequest {
    @NotBlank(message = ResponseMessage.News.MISSING_ID)
    String id;
    @NotNull(message = ResponseMessage.News.MISSING_IMAGE)
    MultipartFile image;
    @JsonIgnore
    String updatedBy;
}
