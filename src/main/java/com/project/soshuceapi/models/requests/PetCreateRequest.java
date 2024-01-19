package com.project.soshuceapi.models.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PetCreateRequest {
    @NotBlank(message = "misssing.pet.name")
    String name;
    @NotBlank(message = "misssing.pet.type")
    int type;
    @NotBlank(message = "misssing.pet.breed")
    String breed;
    @NotBlank(message = "misssing.pet.color")
    String color;
    @NotBlank(message = "misssing.pet.age")
    int age;
    @NotBlank(message = "missing.pet.gender")
    int gender;
    @NotBlank(message = "misssing.pet.image")
    MultipartFile image;
    @NotBlank(message = "misssing.pet.status")
    int status;
    @NotBlank(message = "misssing.pet.weight")
    float weight;
    @NotBlank(message = "misssing.pet.vaccin")
    int vaccin;
    @NotBlank(message = "misssing.pet.sterilization")
    int sterilization;
    @NotBlank(message = "misssing.pet.diet")
    int diet;
    @NotBlank(message = "misssing.pet.rabies")
    int rabies;
    @NotBlank(message = "misssing.pet.toilet")
    int toilet;
    @NotBlank(message = "misssing.pet.friendlyToHuman")
    int friendlyToHuman;
    @NotBlank(message = "misssing.pet.friendlyToDogs")
    int friendlyToDogs;
    @NotBlank(message = "misssing.pet.friendlyToCats")
    int friendlyToCats;
    String description;
}
