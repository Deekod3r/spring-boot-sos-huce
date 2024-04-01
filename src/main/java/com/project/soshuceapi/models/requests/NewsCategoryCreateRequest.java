package com.project.soshuceapi.models.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.soshuceapi.common.ResponseMessage;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewsCategoryCreateRequest {
    @NotBlank(message = ResponseMessage.NewsCategory.MISSING_NAME)
    String name;
    @NotBlank(message = ResponseMessage.NewsCategory.MISSING_DESCRIPTION)
    String description;
    @JsonIgnore
    String createdBy;
}
