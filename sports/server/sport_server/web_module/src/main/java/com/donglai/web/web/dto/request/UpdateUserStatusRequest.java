package com.donglai.web.web.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import java.util.List;

/**
 * @author Moon
 * @date 2021-12-21 15:39
 */
@Data
@ApiModel("修改用户状态")
public class UpdateUserStatusRequest {

    @ApiParam(value = "用户id", example = "0")
    private List<String> userIds;

    @ApiParam(value = "0正常 1封禁", example = "0")
    private int status;
}
