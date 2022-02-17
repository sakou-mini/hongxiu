package com.donglai.web.web.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;

/**
 * @author Moon
 * @date 2021-12-31 11:18
 */
@Data
@ApiModel("后台账号操作记录列表")
public class BackOfficeLogFindListRequest {
    @ApiParam(value = "开始时间")
    private Long startTime;
    @ApiParam(value = "结束时间")
    private Long endTime;
    @ApiParam(value = "分页")
    private int page;
    @ApiParam(value = "分页大小")
    private int size;
}
