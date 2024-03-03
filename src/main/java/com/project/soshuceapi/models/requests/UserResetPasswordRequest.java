package com.project.soshuceapi.models.requests;

import com.project.soshuceapi.common.Constants;
import com.project.soshuceapi.common.ResponseMessage;
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
public class UserResetPasswordRequest {
    @NotBlank(message = ResponseMessage.User.MISSING_ID)
    String id;
    @NotBlank(message = ResponseMessage.User.MISSING_VERIFY_CODE)
    String code;
    @NotBlank(message = ResponseMessage.User.MISSING_EMAIL)
    @Email(message = ResponseMessage.User.INVALID_EMAIL)
    @Length(min = 5, max = 100, message = ResponseMessage.User.INVALID_EMAIL)
    String email;
    @NotBlank(message = ResponseMessage.User.MISSING_PASSWORD)
    @Pattern(regexp = Constants.Regex.PASSWORD, message = ResponseMessage.User.INVALID_PASSWORD)
    @Length(min = 8, max = 100, message = ResponseMessage.User.INVALID_PASSWORD)
    String newPassword;
}
