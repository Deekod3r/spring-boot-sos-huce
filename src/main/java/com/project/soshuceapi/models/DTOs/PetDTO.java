package com.project.soshuceapi.models.DTOs;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PetDTO {
    String id;
    String code;
    String name;
    LocalDate intakeDate;
    Integer type;
    String breed;
    String color;
    Integer age;
    Integer gender;
    String image;
    Integer status;
    Float weight;
    Integer vaccine;
    Integer sterilization;
    Integer diet;
    Integer rabies;
    Integer toilet;
    Integer friendlyToHuman;
    Integer friendlyToDogs;
    Integer friendlyToCats;
    String note;
    String description;
    String adoptedBy;
}
