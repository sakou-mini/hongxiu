package com.donglai.web.web.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;

/**
 * @author Moon
 * @date 2021-12-29 10:36
 */
@Data
@ApiModel(value = "账号列表请求")
public class BackOfficeUserFindListRequest {
    @ApiParam(value = "组别")
    private String roleGroup;
    @ApiParam(value = "状态")
    private Boolean status;
    @ApiParam(value = "创建开始时间")
    private Long createdTimeStart;
    @ApiParam(value = "创建结束时间")
    private Long createdTimeEnd;
    @ApiParam(value = "分页")
    private int page;
    @ApiParam(value = "分页大小")
    private int size;

}
