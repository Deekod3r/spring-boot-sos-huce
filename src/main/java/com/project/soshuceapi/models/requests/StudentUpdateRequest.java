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
public class StudentUpdateRequest {
    @NotBlank(message = "Id is required.")
    String id;
    @NotBlank(message = "Student code is required.")
    @Pattern(regexp = Constants.Regex.STUDENT_CODE, message = "Student code is invalid.")
    String studentCode;
    @NotBlank(message = "Name is required.")
    @Pattern(regexp = Constants.Regex.NAME, message = "Name is invalid.")
    String name;
    @NotBlank(message = "Email is required.")
    @Email(message = "Email is invalid.")
    @Pattern(regexp = Constants.Regex.EMAIL, message = "Email is invalid.")
    String email;
    @NotBlank(message = "Faculty is required.")
    String faculty;
    boolean isActivated;
    @JsonIgnore
    String role;
    @JsonIgnore
    String updatedBy;
}
