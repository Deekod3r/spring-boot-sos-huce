package com.project.soshuceapi.models.DTOs;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileDTO {
    Integer id;
    String name;
    String url;
}
