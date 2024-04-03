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

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DonateCreateRequest {
    @NotBlank(message = ResponseMessage.Donate.MISSING_REMITTER)
    String remitter;
    @NotBlank(message = ResponseMessage.Donate.MISSING_PAYEE)
    String payee;
    @NotNull(message = ResponseMessage.Donate.MISSING_AMOUNT)
    BigDecimal amount;
    @NotNull(message = ResponseMessage.Donate.MISSING_DATE)
    @PastOrPresent(message = ResponseMessage.Donate.INVALID_DATE)
    LocalDate date;
    String detail;
    @JsonIgnore
    String createdBy;
}
