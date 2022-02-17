package com.donglai.web.web.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;

/**
 * @author Moon
 * @date 2021-12-29 14:17
 */
@Data
@ApiModel("条件查询")
public class FindConditionListRequest {
    @ApiParam(value = "状态" ,example = "空值 查询所有 true 查询启用的 false 查询禁用的")
    private Boolean status;
    @ApiParam(value = "春哥开始时间")
    private Long startTime;
    @ApiParam(value = "结束时间")
    private Long endTime;
    @ApiParam(value = "分页")
    private int page;
    @ApiParam(value = "分页大小")
    private int size;
}
