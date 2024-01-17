package com.project.soshuceapi.models.DTOs;

import com.project.soshuceapi.common.enums.security.ERole;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDTO {
    String id;
    String phoneNumber;
    String name;
    String email;
    ERole role;
}
