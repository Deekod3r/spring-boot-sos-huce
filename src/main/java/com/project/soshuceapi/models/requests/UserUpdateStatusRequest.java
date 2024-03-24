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
public class UserUpdateStatusRequest {
    @NotBlank(message = ResponseMessage.User.MISSING_ID)
    String id;
    @NotNull(message = ResponseMessage.User.MISSING_STATUS)
    Boolean status;
    @JsonIgnore
    String updatedBy;
}
