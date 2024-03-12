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

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdoptUpdateStatusRequest {
    @NotBlank(message = ResponseMessage.Adopt.MISSING_ID)
    String id;
    @NotNull(message = ResponseMessage.Adopt.MISSING_STATUS)
    Integer status;
    String message;
    @JsonIgnore
    String updatedBy;
}
