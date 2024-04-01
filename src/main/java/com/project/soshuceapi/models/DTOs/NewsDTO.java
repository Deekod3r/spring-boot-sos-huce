package com.project.soshuceapi.models.DTOs;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewsDTO {
    String id;
    String title;
    String content;
    String image;
    Boolean status;
    String categoryId;
    String categoryName;
    LocalDateTime createdAt;
}
