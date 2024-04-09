package com.project.soshuceapi.models.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.soshuceapi.common.ResponseMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LivingCostCreateRequest {
    @NotBlank(message = ResponseMessage.LivingCost.MISSING_NAME)
    @Length(min = 1, max = 100, message = ResponseMessage.LivingCost.INVALID_NAME)
    String name;
    @NotNull(message = ResponseMessage.LivingCost.MISSING_COST)
    BigDecimal cost;
    @NotNull(message = ResponseMessage.LivingCost.MISSING_DATE)
    @PastOrPresent(message = ResponseMessage.LivingCost.INVALID_DATE)
    LocalDate date;
    @NotEmpty(message = ResponseMessage.LivingCost.MISSING_IMAGES)
    List<MultipartFile> images;
    String note;
    @JsonIgnore
    String createdBy;
}
