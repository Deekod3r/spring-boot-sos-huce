package com.project.soshuceapi.models.DTOs;

import com.project.soshuceapi.entities.logging.ActionLogDetail;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class ActionLogDTO {
    String action;
    String description;
    String createdBy;
    List<ActionLogDetail> details;
}
