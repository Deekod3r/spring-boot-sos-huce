package com.project.soshuceapi.services;

import com.project.soshuceapi.common.Constants;
import com.project.soshuceapi.common.ResponseMessage;
import com.project.soshuceapi.entities.config.Config;
import com.project.soshuceapi.entities.config.ConfigValue;
import com.project.soshuceapi.entities.logging.ActionLogDetail;
import com.project.soshuceapi.exceptions.BadRequestException;
import com.project.soshuceapi.models.DTOs.ActionLogDTO;
import com.project.soshuceapi.models.DTOs.ConfigDTO;
import com.project.soshuceapi.models.requests.ConfigUpdateRequest;
import com.project.soshuceapi.repositories.ConfigRepo;
import com.project.soshuceapi.repositories.ConfigValueRepo;
import com.project.soshuceapi.services.iservice.IActionLogService;
import com.project.soshuceapi.services.iservice.IConfigService;
import com.project.soshuceapi.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class ConfigService implements IConfigService {

    private final static String TAG = "CONFIG";

    @Autowired
    private ConfigRepo configRepo;
    @Autowired
    private ConfigValueRepo configValueRepo;
    @Autowired
    private IActionLogService actionLogService;

    @Override
    public List<Config> getAll() {
        try {
            return configRepo.findAll();
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public ConfigDTO getByKey(String keyConfig) {
        try {
            Config config = configRepo.findByKey(keyConfig);
            if (config == null) {
                return null;
            }
            return ConfigDTO.builder()
                    .id(config.getId())
                    .key(config.getKey())
                    .description(config.getDescription())
                    .values(configValueRepo.findAllByConfig(keyConfig))
                    .build();
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<ConfigValue> getAllValues(String keyConfig) {
        try {
            return configValueRepo.findAllByConfig(keyConfig);
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updateValue(ConfigUpdateRequest request) {
        try {
            configRepo.findById(request.getId()).orElseThrow(
                    () -> new BadRequestException(ResponseMessage.Config.NOT_FOUND));
            List<ConfigValue> configValues = new ArrayList<>();
            for (ConfigValue value : request.getValues()) {
                ConfigValue configValue = configValueRepo.findById(value.getId()).orElseThrow(
                        () -> new BadRequestException(ResponseMessage.Config.NOT_FOUND));
                if (StringUtil.isNullOrBlank(value.getValue())) {
                    throw new BadRequestException(ResponseMessage.Config.MISSING_VALUE);
                }
                if (!Objects.equals(value.getValue().trim(), configValue.getValue())) {
                    configValue.setValue(value.getValue().trim());
                    configValue.setUpdatedBy(request.getUpdatedBy());
                    configValue.setUpdatedAt(LocalDateTime.now());
                    actionLogService.create(ActionLogDTO.builder()
                            .action(Constants.ActionLog.UPDATE)
                            .description(Constants.ActionLog.UPDATE + "." + TAG)
                            .createdBy(request.getUpdatedBy())
                            .details(List.of(
                                    ActionLogDetail.builder()
                                            .tableName(TAG)
                                            .rowId(value.getId().toString())
                                            .columnName("value")
                                            .oldValue(configValue.getValue())
                                            .newValue(value.getValue().trim())
                                            .build()
                            ))
                            .build());
                    configValues.add(configValue);
                }
                configValueRepo.saveAll(configValues);
            }
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

}
