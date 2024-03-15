package com.project.soshuceapi.models.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.soshuceapi.common.ResponseMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
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
public class PetCareLogUpdateRequest {
    @NotNull(message = ResponseMessage.PetCareLog.MISSING_DATE)
    @PastOrPresent(message = ResponseMessage.PetCareLog.INVALID_DATE)
    LocalDate date;
    @NotBlank(message = ResponseMessage.PetCareLog.MISSING_NOTE)
    String note;
    @NotBlank(message = ResponseMessage.PetCareLog.MISSING_ID)
    String id;
    @JsonIgnore
    String updatedBy;
}
