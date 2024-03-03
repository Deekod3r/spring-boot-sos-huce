package com.project.soshuceapi.models.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.soshuceapi.common.Constants;
import com.project.soshuceapi.common.ResponseMessage;
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
public class PetCreateRequest {
    @NotBlank(message = ResponseMessage.Pet.MISSING_NAME)
    @Length(min = 2, max = 50, message = ResponseMessage.Pet.INVALID_NAME)
    @Pattern(regexp = Constants.Regex.NAME, message = ResponseMessage.Pet.INVALID_NAME)
    String name;
    @NotNull(message = ResponseMessage.Pet.MISSING_TYPE)
    @Min(value = 1, message = ResponseMessage.Pet.INVALID_TYPE)
    @Max(value = 3, message = ResponseMessage.Pet.INVALID_TYPE)
    Integer type;
    @NotBlank(message = ResponseMessage.Pet.MISSING_BREED)
    @Length(min = 2, max = 100, message = ResponseMessage.Pet.INVALID_BREED)
    String breed;
    @NotBlank(message = ResponseMessage.Pet.MISSING_COLOR)
    @Length(min = 2, max = 100, message = ResponseMessage.Pet.INVALID_COLOR)
    String color;
    @NotNull(message = ResponseMessage.Pet.MISSING_AGE)
    @Min(value = 1, message = ResponseMessage.Pet.INVALID_AGE)
    @Max(value = 4, message = ResponseMessage.Pet.INVALID_AGE)
    Integer age;
    @NotNull(message = ResponseMessage.Pet.MISSING_GENDER)
    @Min(value = 1, message = ResponseMessage.Pet.INVALID_GENDER)
    @Max(value = 3, message = ResponseMessage.Pet.INVALID_GENDER)
    Integer gender;
    @NotNull(message = ResponseMessage.Pet.MISSING_IMAGE)
    MultipartFile image;
    @NotNull(message = ResponseMessage.Pet.MISSING_STATUS)
    @Min(value = 1, message = ResponseMessage.Pet.INVALID_STATUS)
    @Max(value = 4, message = ResponseMessage.Pet.INVALID_STATUS)
    Integer status;
    @NotNull(message = ResponseMessage.Pet.MISSING_WEIGHT)
    @Min(value = 0, message = ResponseMessage.Pet.INVALID_WEIGHT)
    Float weight;
    @NotNull(message = ResponseMessage.Pet.MISSING_VACCINE)
    @Min(value = 1, message = ResponseMessage.Pet.INVALID_VACCINE)
    @Max(value = 3, message = ResponseMessage.Pet.INVALID_VACCINE)
    Integer vaccine;
    @NotNull(message = ResponseMessage.Pet.MISSING_STERILIZATION)
    @Min(value = 1, message = ResponseMessage.Pet.INVALID_STERILIZATION)
    @Max(value = 3, message = ResponseMessage.Pet.INVALID_STERILIZATION)
    Integer sterilization;
    @NotNull(message = ResponseMessage.Pet.MISSING_DIET)
    @Min(value = 1, message = ResponseMessage.Pet.INVALID_DIET)
    @Max(value = 3, message = ResponseMessage.Pet.INVALID_DIET)
    Integer diet;
    @NotNull(message = ResponseMessage.Pet.MISSING_RABIES)
    @Min(value = 1, message = ResponseMessage.Pet.INVALID_RABIES)
    @Max(value = 3, message = ResponseMessage.Pet.INVALID_RABIES)
    Integer rabies;
    @NotNull(message = ResponseMessage.Pet.MISSING_TOILET)
    @Min(value = 1, message = ResponseMessage.Pet.INVALID_TOILET)
    @Max(value = 3, message = ResponseMessage.Pet.INVALID_TOILET)
    Integer toilet;
    @NotNull(message = ResponseMessage.Pet.MISSING_FRIENDLY_TO_HUMAN)
    @Min(value = 1, message = ResponseMessage.Pet.INVALID_FRIENDLY_TO_HUMAN)
    @Max(value = 3, message = ResponseMessage.Pet.INVALID_FRIENDLY_TO_HUMAN)
    Integer friendlyToHuman;
    @NotNull(message = ResponseMessage.Pet.MISSING_FRIENDLY_TO_DOGS)
    @Min(value = 1, message = ResponseMessage.Pet.INVALID_FRIENDLY_TO_DOGS)
    @Max(value = 3, message = ResponseMessage.Pet.INVALID_FRIENDLY_TO_DOGS)
    Integer friendlyToDogs;
    @NotNull(message = ResponseMessage.Pet.MISSING_FRIENDLY_TO_CATS)
    @Min(value = 1, message = ResponseMessage.Pet.INVALID_FRIENDLY_TO_CATS)
    @Max(value = 3, message = ResponseMessage.Pet.INVALID_FRIENDLY_TO_CATS)
    Integer friendlyToCats;
    String note;
    @NotBlank(message = ResponseMessage.Pet.MISSING_DESCRIPTION)
    String description;
    @JsonIgnore
    String createdBy;
}
