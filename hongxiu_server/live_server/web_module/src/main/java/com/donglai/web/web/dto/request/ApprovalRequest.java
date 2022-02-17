package com.donglai.web.web.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("审核请求")
public class ApprovalRequest {
    @ApiParam(value = "id列表", example = "{1,2,3}")
    private List<Long> ids = new ArrayList<>();
    @ApiParam(value = "审核状态-> 1 审核通过  2 审核不通过", example = "1")
    private int status;
    @ApiParam(value = "拒绝原因", example = "1")
    private String refuseReason;
}
