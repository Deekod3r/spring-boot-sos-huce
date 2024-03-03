package com.project.soshuceapi.models.DTOs;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdoptDTO implements Serializable {
    String id;
    String code;
    Integer wardId;
    String wardName;
    Integer districtId;
    String districtName;
    Integer provinceId;
    String provinceName;
    String address;
    Integer status;
    String reason;
    LocalDateTime confirmedAt;
    LocalDateTime rejectedAt;
    String rejectedReason;
    LocalDateTime createdAt;

    String petId;
    String petName;
    String createdBy;
    String nameCreatedBy;
    String registeredBy;
    String nameRegisteredBy;
    String emailRegisteredBy;
    String phoneRegisteredBy;
    String confirmedBy;
    String nameConfirmedBy;
    String rejectedBy;
    String nameRejectedBy;
}
