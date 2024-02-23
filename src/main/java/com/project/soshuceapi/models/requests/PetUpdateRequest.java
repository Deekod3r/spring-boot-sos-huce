package com.project.soshuceapi.models.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.soshuceapi.common.Constants;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PetUpdateRequest {
    @NotBlank(message = "misssing.pet.id")
    String id;
    @NotBlank(message = "misssing.pet.name")
    @Length(min = 2, max = 50, message = "invalid.length.pet.name")
    @Pattern(regexp = Constants.Regex.NAME, message = "invalid.pet.name")
    String name;
    @NotNull(message = "misssing.pet.type")
    @Min(value = 1, message = "invalid.pet.type")
    @Max(value = 3, message = "invalid.pet.type")
    Integer type;
    @NotBlank(message = "misssing.pet.breed")
    @Length(min = 2, max = 100, message = "invalid.length.pet.breed")
    String breed;
    @NotBlank(message = "misssing.pet.color")
    @Length(min = 2, max = 100, message = "invalid.length.pet.color")
    String color;
    @NotNull(message = "misssing.pet.age")
    @Min(value = 1, message = "invalid.pet.age")
    @Max(value = 4, message = "invalid.pet.age")
    Integer age;
    @NotNull(message = "missing.pet.gender")
    @Min(value = 1, message = "invalid.pet.gender")
    @Max(value = 3, message = "invalid.pet.gender")
    Integer gender;
    @NotNull(message = "misssing.pet.status")
    @Min(value = 1, message = "invalid.pet.status")
    @Max(value = 4, message = "invalid.pet.status")
    Integer status;
    @NotNull(message = "misssing.pet.weight")
    @Min(value = 0, message = "invalid.pet.weight")
    Float weight;
    @NotNull(message = "misssing.pet.vaccin")
    @Min(value = 1, message = "invalid.pet.vaccin")
    @Max(value = 3, message = "invalid.pet.vaccin")
    Integer vaccin;
    @NotNull(message = "misssing.pet.sterilization")
    @Min(value = 1, message = "invalid.pet.sterilization")
    @Max(value = 3, message = "invalid.pet.sterilization")
    Integer sterilization;
    @NotNull(message = "misssing.pet.diet")
    @Min(value = 1, message = "invalid.pet.diet")
    @Max(value = 3, message = "invalid.pet.diet")
    Integer diet;
    @NotNull(message = "misssing.pet.rabies")
    @Min(value = 1, message = "invalid.pet.rabies")
    @Max(value = 3, message = "invalid.pet.rabies")
    Integer rabies;
    @NotNull(message = "misssing.pet.toilet")
    @Min(value = 1, message = "invalid.pet.toilet")
    @Max(value = 3, message = "invalid.pet.toilet")
    Integer toilet;
    @NotNull(message = "misssing.pet.friendlyToHuman")
    @Min(value = 1, message = "invalid.pet.friendlyToHuman")
    @Max(value = 3, message = "invalid.pet.friendlyToHuman")
    Integer friendlyToHuman;
    @NotNull(message = "misssing.pet.friendlyToDogs")
    @Min(value = 1, message = "invalid.pet.friendlyToDogs")
    @Max(value = 3, message = "invalid.pet.friendlyToDogs")
    Integer friendlyToDogs;
    @NotNull(message = "misssing.pet.friendlyToCats")
    @Min(value = 1, message = "invalid.pet.friendlyToCats")
    @Max(value = 3, message = "invalid.pet.friendlyToCats")
    Integer friendlyToCats;
    String note;
    @NotBlank(message = "misssing.pet.description")
    String description;
    @JsonIgnore
    String updatedBy;
}
