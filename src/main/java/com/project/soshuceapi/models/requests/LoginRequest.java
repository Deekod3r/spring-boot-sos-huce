package com.project.soshuceapi.models.requests;

import com.project.soshuceapi.common.ResponseMessage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRequest {
    @NotBlank(message = ResponseMessage.User.MISSING_EMAIL)
    @Email(message = ResponseMessage.User.INVALID_EMAIL)
    String email;
    @NotBlank(message = ResponseMessage.User.MISSING_PASSWORD)
    String password;
}
