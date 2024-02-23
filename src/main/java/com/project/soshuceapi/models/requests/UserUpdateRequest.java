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
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    @NotBlank(message = "missing.user.id")
    String id;
    @NotBlank(message = "missing.user.phone.number")
    @Pattern(regexp = Constants.Regex.PHONE_NUMBER, message = "invalid.user.phone.number")
    @Length(min = 10, max = 15, message = "invalid.length.user.phone.number")
    String phoneNumber;
    @NotBlank(message = "missing.user.name")
    @Pattern(regexp = Constants.Regex.NAME, message = "invalid.user.name")
    @Length(min = 2, max = 100, message = "invalid.length.user.name")
    String name;
    @NotBlank(message = "missing.user.email")
    @Email(message = "invalid.user.email")
    @Length(min = 5, max = 100, message = "invalid.length.user.email")
    String email;
    Boolean isActivated;
    @JsonIgnore
    String role;
    @JsonIgnore
    String updatedBy;
}
