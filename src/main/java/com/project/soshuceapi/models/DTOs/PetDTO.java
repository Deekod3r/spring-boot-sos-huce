package com.project.soshuceapi.models.DTOs;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PetDTO {
    String id;
    String code;
    String name;
    int type;
    String breed;
    String color;
    int age;
    int gender;
    String image;
    int status;
    float weight;
    int vaccin;
    int sterilization;
    int diet;
    int rabies;
    int toilet;
    int friendlyToHuman;
    int friendlyToDogs;
    int friendlyToCats;
    String note;
    String description;
}
