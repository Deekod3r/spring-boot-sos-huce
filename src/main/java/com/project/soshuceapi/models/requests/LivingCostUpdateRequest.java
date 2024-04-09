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
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LivingCostUpdateRequest {
    @NotBlank(message = ResponseMessage.LivingCost.MISSING_ID)
    String id;
    @NotBlank(message = ResponseMessage.LivingCost.MISSING_NAME)
    @Length(min = 1, max = 100, message = ResponseMessage.LivingCost.INVALID_NAME)
    String name;
    @NotNull(message = ResponseMessage.LivingCost.MISSING_COST)
    BigDecimal cost;
    @NotNull(message = ResponseMessage.LivingCost.MISSING_DATE)
    @PastOrPresent(message = ResponseMessage.LivingCost.INVALID_DATE)
    LocalDate date;
    @NotNull(message = ResponseMessage.LivingCost.MISSING_STATUS)
    Boolean status;
    String note;
    @JsonIgnore
    String updatedBy;
}
