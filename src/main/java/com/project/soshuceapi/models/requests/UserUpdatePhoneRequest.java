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
public class UserUpdatePhoneRequest {
    @NotBlank(message = ResponseMessage.User.MISSING_ID)
    String id;
    @NotBlank(message = ResponseMessage.User.MISSING_PASSWORD)
    String currentPassword;
    @NotBlank(message = ResponseMessage.User.MISSING_PHONE_NUMBER)
    @Pattern(regexp = Constants.Regex.PHONE_NUMBER, message = ResponseMessage.User.INVALID_PHONE_NUMBER)
    @Length(min = 10, max = 15, message = ResponseMessage.User.INVALID_PHONE_NUMBER)
    String phoneNumber;
    @JsonIgnore
    String updatedBy;
}
