package com.project.soshuceapi.models.requests;

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
public class UserResetPasswordRequest {
    @NotBlank(message = "missing.request.id")
    String id;
    @NotBlank(message = "missing.verify.code")
    String code;
    @NotBlank(message = "missing.user.email")
    @Email(message = "invalid.user.email")
    String email;
    @NotBlank(message = "missing.user.new.password")
    @Pattern(regexp = Constants.Regex.PASSWORD, message = "invalid.user.password")
    String newPassword;
}
