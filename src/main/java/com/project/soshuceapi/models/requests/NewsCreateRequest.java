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
public class NewsCreateRequest {
    @NotBlank(message = ResponseMessage.News.MISSING_TITLE)
    String title;
    @NotBlank(message = ResponseMessage.News.MISSING_CONTENT)
    String content;
    @NotNull(message = ResponseMessage.News.MISSING_CATEGORY)
    String categoryId;
    @NotNull(message = ResponseMessage.News.MISSING_IMAGE)
    MultipartFile image;
    @JsonIgnore
    String createdBy;
}
