package com.project.soshuceapi.models.DTOs;

import lombok.*;

@Data
@Builder
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class FileDTO {
    private int id;
    private String name;
    private String url;
}
