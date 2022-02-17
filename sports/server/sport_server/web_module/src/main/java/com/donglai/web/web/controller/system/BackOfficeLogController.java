package com.donglai.web.web.controller.system;

import com.donglai.web.db.backoffice.service.BackOfficeLogService;
import com.donglai.web.response.RestResponse;
import com.donglai.web.response.SuccessResponse;
import com.donglai.web.web.dto.request.BackOfficeLogFindListRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Moon
 * @date 2021-12-31 11:12
 */
@RestController
@RequestMapping("api/v1/backOfficeLog")
@Api(value = "BackOfficeLogController", tags = "操作日志管理")
public class BackOfficeLogController {

    @Autowired
    private BackOfficeLogService backOfficeLogService;

    @GetMapping("/logList")
    @ApiOperation("后台账号操作记录列表")
    public RestResponse findLogList(BackOfficeLogFindListRequest request) {
        return new SuccessResponse().withData(backOfficeLogService.findLogList(request));
    }
}
