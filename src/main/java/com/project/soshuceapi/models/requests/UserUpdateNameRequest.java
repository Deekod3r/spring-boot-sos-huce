package com.project.soshuceapi.models.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.soshuceapi.common.Constants;
import com.project.soshuceapi.common.ResponseMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateNameRequest {
    @NotBlank(message = ResponseMessage.User.MISSING_ID)
    String id;
    @NotBlank(message = ResponseMessage.User.MISSING_PASSWORD)
    String currentPassword;
    @NotBlank(message = ResponseMessage.User.MISSING_NAME)
    @Pattern(regexp = Constants.Regex.CHARACTER, message = ResponseMessage.User.INVALID_NAME)
    @Length(min = 2, max = 100, message = ResponseMessage.User.INVALID_NAME)
    String name;
    @JsonIgnore
    String updatedBy;
}
