package com.project.soshuceapi.models.requests;

import com.project.soshuceapi.common.ResponseMessage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class FeedbackCreateRequest {
    @NotBlank(message = ResponseMessage.Feedback.MISSING_FULL_NAME)
    @Length(min = 2, max = 100, message = ResponseMessage.Feedback.INVALID_FULL_NAME)
    String fullName;
    @NotBlank(message = ResponseMessage.Feedback.MISSING_MESSAGE)
    @Length(min = 20, message = ResponseMessage.Feedback.INVALID_MESSAGE)
    String message;
    @NotBlank(message = ResponseMessage.Feedback.MISSING_EMAIL)
    @Email(message = ResponseMessage.Feedback.INVALID_EMAIL)
    String email;
}
