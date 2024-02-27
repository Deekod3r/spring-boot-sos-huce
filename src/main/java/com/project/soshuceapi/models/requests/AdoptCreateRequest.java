package com.project.soshuceapi.models.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @NotNull(message = "missing.adopt.ward.id")
    Integer wardId;
    @NotNull(message = "missing.adopt.district.id")
    Integer districtId;
    @NotNull(message = "missing.adopt.province.id")
    Integer provinceId;
    @NotBlank(message = "missing.adopt.address")
    @Length(max = 255, message = "invalid.length.adopt.address")
    String address;
    @NotBlank(message = "missing.adopt.reason")
    @Length(max = 255, message = "invalid.length.adopt.reason")
    String reason;
    @NotNull(message = "missing.adopt.pet.id")
    String petId;
    @NotBlank(message = "missing.adopt.registered.by")
    String registeredBy;
    @JsonIgnore
    String createdBy;
}
