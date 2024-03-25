package com.project.soshuceapi.models.DTOs;

import com.project.soshuceapi.entities.config.ConfigValue;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfigDTO {
    Long id;
    String key;
    String description;
    List<ConfigValue> values;
}
