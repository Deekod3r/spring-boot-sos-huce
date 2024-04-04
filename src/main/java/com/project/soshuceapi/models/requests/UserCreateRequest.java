package com.project.soshuceapi.models.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.soshuceapi.common.Constants;
import com.project.soshuceapi.common.ResponseMessage;
import com.project.soshuceapi.common.enums.security.ERole;
import jakarta.validation.constraints.Email;
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
public class UserCreateRequest {
    @NotBlank(message = ResponseMessage.User.MISSING_PHONE_NUMBER)
    @Pattern(regexp = Constants.Regex.DIGIT, message = ResponseMessage.User.INVALID_PHONE_NUMBER)
    @Length(min = 10, max = 15, message = ResponseMessage.User.INVALID_PHONE_NUMBER)
    String phoneNumber;
    @NotBlank(message = ResponseMessage.User.MISSING_NAME)
    @Pattern(regexp = Constants.Regex.CHARACTER, message = ResponseMessage.User.INVALID_NAME)
    @Length(min = 2, max = 100, message = ResponseMessage.User.INVALID_NAME)
    String name;
    @NotBlank(message = ResponseMessage.User.MISSING_EMAIL)
    @Email(message = ResponseMessage.User.INVALID_EMAIL)
    @Length(min = 5, max = 100, message = ResponseMessage.User.INVALID_EMAIL)
    String email;
    @NotBlank(message = ResponseMessage.User.MISSING_PASSWORD)
    @Pattern(regexp = Constants.Regex.PASSWORD, message = ResponseMessage.User.INVALID_PASSWORD)
    @Length(min = 8, max = 100, message = ResponseMessage.User.INVALID_PASSWORD)
    String password;
    @JsonIgnore
    ERole role;
    @JsonIgnore
    String createdBy;
}