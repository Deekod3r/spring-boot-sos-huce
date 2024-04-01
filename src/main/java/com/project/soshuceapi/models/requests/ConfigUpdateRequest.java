package com.project.soshuceapi.models.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.soshuceapi.common.ResponseMessage;
import com.project.soshuceapi.entities.config.ConfigValue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfigUpdateRequest {
    @NotNull(message = ResponseMessage.Config.MISSING_CONFIG_ID)
    Long id;
    @NotEmpty(message = ResponseMessage.Config.MISSING_VALUE)
    List<ConfigValue> values;
    @JsonIgnore
    String updatedBy;
}
