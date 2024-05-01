package com.project.soshuceapi.models.requests;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PetSearchRequest {
    Integer page;
    Integer limit;
    String name;
    String breed;
    String color;
    String code;
    Integer type;
    Integer age;
    Integer gender;
    Integer status;
    Integer diet;
    Integer vaccine;
    Integer sterilization;
    Integer rabies;
    String adoptedBy;
    LocalDate intakeDateFrom;
    LocalDate intakeDateTo;
    Boolean fullData;
}

