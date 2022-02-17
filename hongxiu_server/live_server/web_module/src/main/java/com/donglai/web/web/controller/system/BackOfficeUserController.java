package com.donglai.web.web.controller.system;

import com.donglai.web.db.backoffice.entity.BackOfficeUser;
import com.donglai.web.response.ErrorResponse;
import com.donglai.web.response.GlobalResponseCode;
import com.donglai.web.response.RestResponse;
import com.donglai.web.response.SuccessResponse;
import com.donglai.web.service.BackOfficeUserBackService;
import com.donglai.web.service.BackOfficeUserProcessService;
import com.donglai.web.web.dto.reply.BackOfficeUserDto;
import com.donglai.web.web.dto.request.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "用户管理", tags = "backofficeUserController")
@RequestMapping("/api/v1/backofficeUser")
@Slf4j
public class BackOfficeUserController {
    @Autowired
    BackOfficeUserBackService backOfficeUserBackService;
    @Autowired
    BackOfficeUserProcessService backOfficeUserProcessService;

    @ApiOperation(value = "登录")
    @PostMapping(value = "/login")
    public RestResponse login(LoginRequest loginRequest) {
        Object token = null;
        try {
            token = backOfficeUserProcessService.login(loginRequest);
        } catch (AuthenticationException e) {
            return new ErrorResponse(GlobalResponseCode.USERNAME_OR_PASSWORD_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new SuccessResponse().withData(token);
    }

    @ApiOperation(value = "注销")
    @PostMapping(value = "/logout")
    public RestResponse logout() {
        backOfficeUserProcessService.logout();
        return new SuccessResponse();
    }

    @ApiOperation(value = "无权限")
    @GetMapping(value = "/unauthorized")
    public RestResponse unauthorized() {
        return new ErrorResponse(GlobalResponseCode.ACCESS_FORBIDDEN_ERROR);
    }

    @ApiOperation(value = "未登录")
    @GetMapping(value = "/unauth")
    public RestResponse unauth() {
        return new ErrorResponse(GlobalResponseCode.NOT_AUTH_ERROR);
    }

    @ApiOperation(value = "查询后台用户信息")
    @GetMapping(value = "/loginUserInfo")
    public RestResponse loginUserInfo() {
        Subject subject = SecurityUtils.getSubject();
        BackOfficeUser backOfficeUser = (BackOfficeUser) subject.getPrincipal();
        if (backOfficeUser == null) return new ErrorResponse(GlobalResponseCode.USER_NOT_FOUND);
        return new SuccessResponse().withData(BackOfficeUserDto.newInstance(backOfficeUser));
    }


    @ApiOperation(value = "条件查询后台用户列表")
    @PostMapping("/findList")
    public RestResponse findList(BackOfficeUserFindListRequest request) {
        return new SuccessResponse().withData(backOfficeUserBackService.findList(request));
    }

    @ApiOperation(value = "修改后台用户状态")
    @PostMapping("/updateBackUserEnable")
    public RestResponse updateUserEnable(UpdateUserEnableRequest request) {
        return backOfficeUserBackService.updateUserEnable(request);
    }

    @ApiOperation(value = "删除后台用户")
    @PostMapping("/deleteBackUser")
    public RestResponse deleteBackUser(UpdateUserEnableRequest request) {
        return backOfficeUserBackService.deleteBackUser(request);
    }

    @ApiOperation(value = "添加后台用户")
    @PostMapping("/addBackUser")
    public RestResponse addBackUser(AddBackUserRequest request) {
        return backOfficeUserBackService.addBackUser(request);
    }

    @PostMapping("/updateBackOfficeUser")
    public RestResponse updateBackOfficeUser(UpdateBackOfficeUserRequest request) {
        return backOfficeUserBackService.updateBackOfficeUser(request);
    }

    @PostMapping("/updatePassword")
    public RestResponse updatePassword(String id, String oldPassword, String newPassword) {
        return backOfficeUserBackService.updatePassword(id, oldPassword, newPassword);
    }

    @ApiOperation(value = "test2")
    @GetMapping(value = "/test2")
    public RestResponse test2() {
        return new SuccessResponse().withData("测试一下");
    }


}
