package com.project.soshuceapi.models.requests;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LivingCostSearchRequest {
    Integer page;
    Integer limit;
    Integer category;
    Boolean fullData;
    LocalDate fromDate;
    LocalDate toDate;
}
