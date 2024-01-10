package com.project.soshuceapi.models.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.soshuceapi.common.Constants;
import com.project.soshuceapi.common.enums.security.ERole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreateRequest {
    @NotBlank(message = "missing.user.phone.number")
    @Pattern(regexp = Constants.Regex.PHONE_NUMBER, message = "invalid.user.code")
    String phoneNumber;
    @NotBlank(message = "missing.user.name")
    @Pattern(regexp = Constants.Regex.NAME, message = "invalid.user.name")
    String name;
    @NotBlank(message = "missing.user.email")
    @Email(message = "invalid.user.email")
    String email;
    @NotBlank(message = "missing.user.password")
    @Pattern(regexp = Constants.Regex.PASSWORD, message = "invalid.user.password")
    String password;
    @JsonIgnore
    ERole role;
    @JsonIgnore
    String createdBy;
}