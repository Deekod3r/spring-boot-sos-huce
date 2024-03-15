package com.project.soshuceapi.models.DTOs;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PetCareLogDTO {
    String id;
    String adoptId;
    String adoptCode;
    String petName;
    LocalDate date;
    String note;
}
