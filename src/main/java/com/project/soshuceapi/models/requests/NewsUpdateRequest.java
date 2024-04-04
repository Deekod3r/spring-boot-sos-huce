package com.project.soshuceapi.models.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.soshuceapi.common.ResponseMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewsUpdateRequest {
    @NotBlank(message = ResponseMessage.News.MISSING_ID)
    String id;
    @NotBlank(message = ResponseMessage.News.MISSING_TITLE)
    @Length(max = 255, message = ResponseMessage.News.INVALID_TITLE)
    String title;
    @NotBlank(message = ResponseMessage.News.MISSING_DESCRIPTION)
    @Length(max = 255, message = ResponseMessage.News.INVALID_DESCRIPTION)
    String description;
    @NotBlank(message = ResponseMessage.News.MISSING_CONTENT)
    String content;
    @NotNull(message = ResponseMessage.News.MISSING_CATEGORY)
    String categoryId;
    @NotNull(message = ResponseMessage.News.MISSING_STATUS)
    Boolean status;
    @JsonIgnore
    String updatedBy;
}
