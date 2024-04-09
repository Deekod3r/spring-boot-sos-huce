package com.project.soshuceapi.models.DTOs;

import com.project.soshuceapi.entities.Image;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LivingCostDTO {
    String id;
    String name;
    BigDecimal cost;
    LocalDate date;
    Boolean status;
    String note;
    List<Image> images;
}
