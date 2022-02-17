package com.donglai.web.web.controller.system;

import com.donglai.web.db.backoffice.service.BackOfficeLogService;
import com.donglai.web.response.RestResponse;
import com.donglai.web.response.SuccessResponse;
import com.donglai.web.web.dto.request.BackOfficeLogFindListRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Moon
 * @date 2021-12-31 11:12
 */
@RestController
@RequestMapping("api/v1/backOfficeLog")
public class BackOfficeLogController {

    @Autowired
    private BackOfficeLogService backOfficeLogService;

    @PostMapping("/findList")
    public RestResponse findLogList(BackOfficeLogFindListRequest request) {

        return new SuccessResponse().withData(backOfficeLogService.findLogList(request));
    }
}
