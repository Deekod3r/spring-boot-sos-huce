package com.project.soshuceapi.models.requests;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class PetCareLogSearchRequest {
    String adoptId;
    String petId;
    LocalDate fromDate;
    LocalDate toDate;
}
