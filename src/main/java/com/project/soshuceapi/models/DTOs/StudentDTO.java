package com.project.soshuceapi.models.DTOs;

import com.project.soshuceapi.entities.Faculty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentDTO {
    String id;
    String studentCode;
    String name;
    String email;
    Faculty faculty;
}
