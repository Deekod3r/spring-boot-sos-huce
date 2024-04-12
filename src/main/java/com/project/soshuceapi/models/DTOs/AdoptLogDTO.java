package com.project.soshuceapi.models.DTOs;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdoptLogDTO {
    String id;
    String code;
    LocalDate checkDateFirst;
    LocalDate checkDateSecond;
    LocalDate checkDateThird;
}
