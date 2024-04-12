package com.project.soshuceapi.models.DTOs;

import com.project.soshuceapi.entities.Image;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TreatmentDTO {
    String id;
    String name;
    LocalDate startDate;
    LocalDate endDate;
    Integer type;
    String location;
    String description;
    BigDecimal price;
    Integer quantity;
    Boolean status;
    String petId;
    String petCode;
    String petName;
    List<Image> images;
}
