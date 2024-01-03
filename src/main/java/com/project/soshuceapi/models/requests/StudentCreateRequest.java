package com.project.soshuceapi.models.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class StudentCreateRequest {
    @NotBlank(message = "missing.student.code")
    @Pattern(regexp = Constants.Regex.STUDENT_CODE, message = "invalid.student.code")
    String studentCode;
    @NotBlank(message = "missing.student.name")
    @Pattern(regexp = Constants.Regex.NAME, message = "invalid.student.name")
    String name;
    @NotBlank(message = "missing.student.email")
    @Email(message = "invalid.student.email")
    @Pattern(regexp = Constants.Regex.EMAIL, message = "invalid.student.email")
    String email;
    @NotBlank(message = "missing.student.password")
    @Pattern(regexp = Constants.Regex.PASSWORD, message = "invalid.student.password")
    String password;
    @NotBlank(message = "missing.student.faculty")
    String faculty;
    @JsonIgnore
    ERole role;
    @JsonIgnore
    String createdBy;
}