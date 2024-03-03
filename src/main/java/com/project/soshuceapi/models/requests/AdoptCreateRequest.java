package com.project.soshuceapi.models.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.soshuceapi.common.ResponseMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdoptCreateRequest {
    @NotNull(message = ResponseMessage.Adopt.MISSING_WARD_ID)
    Integer wardId;
    @NotNull(message = ResponseMessage.Adopt.MISSING_DISTRICT_ID)
    Integer districtId;
    @NotNull(message = ResponseMessage.Adopt.MISSING_PROVINCE_ID)
    Integer provinceId;
    @NotBlank(message = ResponseMessage.Adopt.MISSING_ADDRESS)
    @Length(max = 255, message = ResponseMessage.Adopt.INVALID_ADDRESS)
    String address;
    @NotBlank(message = ResponseMessage.Adopt.MISSING_REASON)
    @Length(max = 255, message = ResponseMessage.Adopt.INVALID_REASON)
    String reason;
    @NotNull(message = ResponseMessage.Pet.MISSING_ID)
    String petId;
    @NotBlank(message = ResponseMessage.Adopt.MISSING_REGISTERED_BY)
    String registeredBy;
    @JsonIgnore
    String createdBy;
}
