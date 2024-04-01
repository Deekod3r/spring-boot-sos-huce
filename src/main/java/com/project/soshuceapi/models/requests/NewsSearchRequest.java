package com.project.soshuceapi.models.requests;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewsSearchRequest {
    String title;
    Boolean status;
    String categoryId;
    String fromDate;
    String toDate;
}
