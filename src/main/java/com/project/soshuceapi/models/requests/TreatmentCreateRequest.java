package com.project.soshuceapi.models.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.soshuceapi.common.ResponseMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TreatmentCreateRequest {
    @NotBlank(message = ResponseMessage.Treatment.MISSING_LOCATION)
    @Length(max = 255, message = ResponseMessage.Treatment.INVALID_LOCATION)
    String location;
    @NotBlank(message = ResponseMessage.Treatment.MISSING_PET_ID)
    String petId;
    @NotEmpty(message = ResponseMessage.Treatment.MISSING_DETAILS)
    List<TreatmentCreateDetailPetRequest> detailPet;
    @NotEmpty(message = ResponseMessage.Treatment.MISSING_IMAGES)
    List<MultipartFile> images;
    @JsonIgnore
    String createdBy;
}
