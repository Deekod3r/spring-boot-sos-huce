package com.project.soshuceapi.services.iservice;

import com.project.soshuceapi.entities.logging.ActionLog;
import com.project.soshuceapi.entities.logging.ActionLogDetail;
import com.project.soshuceapi.models.DTOs.ActionLogDTO;

public interface IActionLogService {

    void create(ActionLogDTO actionLogDTO);

    void createDetail(ActionLogDetail actionLogDetail);

}
