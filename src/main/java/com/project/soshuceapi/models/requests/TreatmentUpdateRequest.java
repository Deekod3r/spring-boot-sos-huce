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
public class TreatmentUpdateRequest {
    @NotBlank(message = ResponseMessage.Treatment.MISSING_ID)
    String id;
    @NotBlank(message = ResponseMessage.Treatment.MISSING_LOCATION)
    String location;
    @NotBlank(message = ResponseMessage.Treatment.MISSING_NAME)
    @Length(max = 100, message = ResponseMessage.Treatment.INVALID_NAME)
    String name;
    @NotNull(message = ResponseMessage.Treatment.MISSING_START_DATE)
    @PastOrPresent(message = ResponseMessage.Treatment.INVALID_START_DATE)
    LocalDate startDate;
    @NotNull(message = ResponseMessage.Treatment.MISSING_END_DATE)
    @PastOrPresent(message = ResponseMessage.Treatment.INVALID_END_DATE)
    LocalDate endDate;
    String description;
    @NotNull(message = ResponseMessage.Treatment.MISSING_PRICE)
    BigDecimal price;
    @NotNull(message = ResponseMessage.Treatment.MISSING_QUANTITY)
    Integer quantity;
    @NotNull(message = ResponseMessage.Treatment.MISSING_TYPE)
    Integer type;
    @NotNull(message = ResponseMessage.Treatment.MISSING_STATUS)
    Boolean status;
    @JsonIgnore
    String updatedBy;
}
