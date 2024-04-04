package com.project.soshuceapi.models.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.soshuceapi.common.ResponseMessage;
import jakarta.validation.constraints.NotBlank;
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
public class BankCreateRequest {
    @NotBlank(message = ResponseMessage.Bank.MISSING_NAME)
    @Length(max = 100, message = ResponseMessage.Bank.INVALID_NAME)
    String name;
    @NotBlank(message = ResponseMessage.Bank.MISSING_ACCOUNT_NUMBER)
    @Length(max = 100, message = ResponseMessage.Bank.INVALID_ACCOUNT_NUMBER)
    String accountNumber;
    @NotBlank(message = ResponseMessage.Bank.MISSING_OWNER)
    @Length(max = 100, message = ResponseMessage.Bank.INVALID_OWNER)
    String owner;
    @NotBlank(message = ResponseMessage.Bank.MISSING_LOGO)
    @Length(max = 100, message = ResponseMessage.Bank.INVALID_LOGO)
    String logo;
    @JsonIgnore
    String createdBy;
}
