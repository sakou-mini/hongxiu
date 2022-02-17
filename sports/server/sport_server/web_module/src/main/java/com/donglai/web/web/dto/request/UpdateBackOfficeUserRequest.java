package com.donglai.web.web.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;

/**
 * @author Moon
 * @date 2021-12-31 15:52
 */
@Data
@ApiModel(value = "修改后台账号请求")
public class UpdateBackOfficeUserRequest {
    @ApiParam(value = "账号id")
    private String id;
    @ApiParam(value = "账号昵称")
    private String nickname;
    @ApiParam(value = "组别id")
    private String roleId;
}
