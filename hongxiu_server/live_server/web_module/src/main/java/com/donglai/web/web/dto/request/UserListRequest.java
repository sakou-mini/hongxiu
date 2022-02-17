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

    @ApiParam(value = "登录方式")
    private String loginType;

    @ApiParam(value = "状态")
    private Boolean status;

    @ApiParam(value = "最近登录时间范围-开始")
    private Long lastLoginTimeStart;

    @ApiParam(value = "最近登录时间范围-结束")
    private Long lastLoginTimeEnd;

    @ApiParam(value = "注册时间范围查找-开始")
    private Long joinTimeStart;

    @ApiParam(value = "注册时间范围查找-结束")
    private Long joinTimeEnd;

    @ApiParam(value = "当前页")
    private Integer page;

    @ApiParam(value = "页面大小")
    private Integer size;
}
