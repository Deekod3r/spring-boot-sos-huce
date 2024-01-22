package com.project.soshuceapi.models.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
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
    String description;
}
