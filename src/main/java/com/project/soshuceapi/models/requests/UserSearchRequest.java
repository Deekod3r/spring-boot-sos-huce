package com.project.soshuceapi.models.requests;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSearchRequest {
    String name;
    String email;
    String phoneNumber;
    String role;
    Boolean isActivated;
    Integer page;
    Integer limit;
}
