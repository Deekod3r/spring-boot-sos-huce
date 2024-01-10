package com.project.soshuceapi.models.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.soshuceapi.common.Constants;
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
public class UserUpdateRequest {
    @NotBlank(message = "missing.user.id")
    String id;
    @NotBlank(message = "missing.user.phone.number")
    @Pattern(regexp = Constants.Regex.PHONE_NUMBER, message = "invalid.user.phone.number")
    String phoneNumber;
    @NotBlank(message = "missing.user.name")
    @Pattern(regexp = Constants.Regex.NAME, message = "invalid.user.name")
    String name;
    @NotBlank(message = "missing.user.email")
    @Email(message = "invalid.user.email")
    String email;
    boolean isActivated;
    @JsonIgnore
    String role;
    @JsonIgnore
    String updatedBy;
}
