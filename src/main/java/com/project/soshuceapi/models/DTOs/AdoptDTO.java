package com.project.soshuceapi.models.DTOs;

import com.project.soshuceapi.entities.Pet;
import com.project.soshuceapi.entities.User;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdoptDTO {
    String id;
    String code;
    Integer wardId;
    Integer districtId;
    Integer provinceId;
    String address;
    Integer status;
    String reason;
    LocalDateTime confirmedAt;
    LocalDateTime rejectedAt;
    String rejectedReason;

    Boolean isDeleted;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    LocalDateTime deletedAt;
    String updatedBy;
    String deletedBy;

    Pet pet;
    User createdBy;
    User confirmedBy;
    User rejectedBy;
}
