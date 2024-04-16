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
public class TreatmentSearchRequest {
    String petId;
    Boolean status;
    Integer page;
    Integer limit;
    Boolean fullData;
    Integer type;
    Integer daysOfTreatment;
}
