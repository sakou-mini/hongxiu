package com.donglai.web.web.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;

@Data
@ApiModel("用户列表请求")
public class UserListRequest {
    @ApiParam(value = "用户id")
    private String userId;
    @ApiParam(value = "手机号")
    private String phone;
    @ApiParam(value = "用户名")
    private String nickname;
    @ApiParam(value = "当前页")
    private int page;
    @ApiParam(value = "页面大小")
    private int size;
}
