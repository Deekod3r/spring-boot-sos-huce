package com.project.soshuceapi.models.requests;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DonateSearchRequest {
    String remitter;
    String payee;
    LocalDate fromDate;
    LocalDate toDate;
    Integer limit;
    Integer page;
    Boolean fullData;
}
