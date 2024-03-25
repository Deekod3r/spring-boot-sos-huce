package com.project.soshuceapi.models.DTOs;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GalleriaDTO {
    String id;
    String title;
    String link;
    String description;
    String image;
    Boolean status;
    Integer index;
}
