package com.donglai.web.db.backoffice.service;

import com.donglai.web.db.backoffice.entity.BackOfficeLog;
import com.donglai.web.db.backoffice.repository.BackOfficeLogRepository;
import com.donglai.web.db.server.service.BackOfficeLogDbService;
import com.donglai.web.response.PageInfo;
import com.donglai.web.web.dto.request.BackOfficeLogFindListRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Moon
 * @date 2021-12-30 11:52
 */
@Service
public class BackOfficeLogService {

    @Autowired
    private BackOfficeLogDbService backOfficeLogDbService;
    @Autowired
    private BackOfficeLogRepository backOfficeLogRepository;

    public BackOfficeLog save(BackOfficeLog backOfficeLog) {
        return backOfficeLogRepository.save(backOfficeLog);
    }


    public PageInfo<BackOfficeLog> findLogList(BackOfficeLogFindListRequest request) {
        return backOfficeLogDbService.conditionGetLabel(request);
    }
}
