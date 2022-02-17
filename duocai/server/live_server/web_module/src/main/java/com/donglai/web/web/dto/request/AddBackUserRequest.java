package com.donglai.web.web.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;

/**
 * @author Moon
 * @date 2021-12-29 14:01
 */
@Data
@ApiModel(value = "添加后台账号请求")
public class AddBackUserRequest {
    @ApiParam(value = "账号显示昵称")
    private String nickname;
    @ApiParam(value = "账号名（登陆时使用）")
    private String username;
    @ApiParam(value = "密码")
    private String pwd;
    @ApiParam(value = "组别id")
    private String roleId;
    @ApiParam(value = "账号状态")
    private Boolean status;
}
