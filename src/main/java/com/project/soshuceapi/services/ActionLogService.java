package com.project.soshuceapi.services;

import com.project.soshuceapi.entities.logging.ActionLog;
import com.project.soshuceapi.entities.logging.ActionLogDetail;
import com.project.soshuceapi.models.DTOs.ActionLogDTO;
import com.project.soshuceapi.repositories.ActionLogDetailRepo;
import com.project.soshuceapi.repositories.ActionLogRepo;
import com.project.soshuceapi.services.iservice.IActionLogService;
import com.project.soshuceapi.utils.CollectionUtil;
import com.project.soshuceapi.utils.DataUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class ActionLogService implements IActionLogService {

    private static final String TAG = "ACTION_LOG";

    @Autowired
    private ActionLogRepo actionLogRepo;
    @Autowired
    private ActionLogDetailRepo actionLogDetailRepo;

    @Override
    public void create(ActionLogDTO actionLogDTO) {
        try {
            ActionLog actionLog = ActionLog.builder()
                    .action(actionLogDTO.getAction())
                    .description(actionLogDTO.getDescription())
                    .ip(DataUtil.getIP())
                    .createdBy(actionLogDTO.getCreatedBy())
                    .createdAt(LocalDateTime.now())
                    .build();
            actionLog = actionLogRepo.save(actionLog);
            if (!CollectionUtil.isNullOrEmpty(actionLogDTO.getDetails())) {
                ActionLog finalActionLog = actionLog;
                actionLogDTO.getDetails().forEach(detail -> {
                    detail.setActionLogId(finalActionLog.getId());
                    createDetail(detail);
                });
            }
        } catch (Exception e) {
            log.error(TAG + ": error.create.action.log");
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void createDetail(ActionLogDetail actionLogDetail) {
        try {
            actionLogDetailRepo.save(actionLogDetail);
        } catch (Exception e) {
            log.error(TAG + ": error.create.action.log.detail");
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

}
