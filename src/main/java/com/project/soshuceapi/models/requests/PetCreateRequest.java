package com.project.soshuceapi.models.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "misssing.pet.type")
    int type;
    @NotBlank(message = "misssing.pet.breed")
    String breed;
    @NotBlank(message = "misssing.pet.color")
    String color;
    @NotNull(message = "misssing.pet.age")
    int age;
    @NotNull(message = "missing.pet.gender")
    int gender;
    @NotNull(message = "misssing.pet.image")
    MultipartFile image;
    @NotNull(message = "misssing.pet.status")
    int status;
    @NotNull(message = "misssing.pet.weight")
    float weight;
    @NotNull(message = "misssing.pet.vaccin")
    int vaccin;
    @NotNull(message = "misssing.pet.sterilization")
    int sterilization;
    @NotNull(message = "misssing.pet.diet")
    int diet;
    @NotNull(message = "misssing.pet.rabies")
    int rabies;
    @NotNull(message = "misssing.pet.toilet")
    int toilet;
    @NotNull(message = "misssing.pet.friendlyToHuman")
    int friendlyToHuman;
    @NotNull(message = "misssing.pet.friendlyToDogs")
    int friendlyToDogs;
    @NotNull(message = "misssing.pet.friendlyToCats")
    int friendlyToCats;
    String note;
    @NotBlank(message = "misssing.pet.description")
    String description;
    @JsonIgnore
    String createdBy;
}
