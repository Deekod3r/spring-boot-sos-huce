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
public class AdoptSearchRequest {
    String code;
    String fromDate;
    String toDate;
    Integer status;
    String registeredBy;
    String petAdopt;
    Integer page;
    Integer limit;
}
