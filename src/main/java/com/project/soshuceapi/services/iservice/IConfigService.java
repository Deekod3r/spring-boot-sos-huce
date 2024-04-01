package com.project.soshuceapi.services.iservice;

import com.project.soshuceapi.entities.config.Config;
import com.project.soshuceapi.entities.config.ConfigValue;
import com.project.soshuceapi.models.DTOs.ConfigDTO;
import com.project.soshuceapi.models.requests.ConfigUpdateRequest;

import java.util.List;

public interface IConfigService {

    List<Config> getAll();

    ConfigDTO getByKey(String keyConfig);

    List<ConfigValue> getAllValues(String keyConfig);

    void updateValue(ConfigUpdateRequest request);

}
