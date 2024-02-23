package com.project.soshuceapi.services;

import com.project.soshuceapi.entities.logging.ActionLog;
import com.project.soshuceapi.entities.logging.ActionLogDetail;
import com.project.soshuceapi.models.DTOs.ActionLogDTO;
import com.project.soshuceapi.repositories.ActionLogDetailRepository;
import com.project.soshuceapi.repositories.ActionLogRepository;
import com.project.soshuceapi.services.iservice.IActionLogService;
import com.project.soshuceapi.utils.CollectionUtil;
import com.project.soshuceapi.utils.DataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ActionLogService implements IActionLogService {

    private final static String TAG = "ACTION_LOG";

    @Autowired
    private ActionLogRepository actionLogRepository;
    @Autowired
    private ActionLogDetailRepository actionLogDetailRepository;

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
            actionLog = actionLogRepository.save(actionLog);
            if (!CollectionUtil.isNullOrEmpty(actionLogDTO.getDetails())) {
                ActionLog finalActionLog = actionLog;
                actionLogDTO.getDetails().forEach(detail -> {
                    detail.setActionLogId(finalActionLog.getId());
                    createDetail(detail);
                });
            }
        } catch (Exception e) {
            throw new RuntimeException("error.create.action.log");
        }
    }

    @Override
    public void createDetail(ActionLogDetail actionLogDetail) {
        try {
            actionLogDetailRepository.save(actionLogDetail);
        } catch (Exception e) {
            throw new RuntimeException("error.create.action.log.detail");
        }
    }

}
