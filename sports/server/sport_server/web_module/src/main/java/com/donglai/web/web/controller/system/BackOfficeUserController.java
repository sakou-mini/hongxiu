package com.donglai.web.web.controller.system;

import com.donglai.web.config.annotation.BackLog;
import com.donglai.web.db.backoffice.entity.BackOfficeUser;
import com.donglai.web.response.ErrorResponse;
import com.donglai.web.response.GlobalResponseCode;
import com.donglai.web.response.RestResponse;
import com.donglai.web.response.SuccessResponse;
import com.donglai.web.service.BackOfficeUserBackService;
import com.donglai.web.service.BackOfficeUserProcessService;
import com.donglai.web.util.ConvertUtils;
import com.donglai.web.web.dto.request.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.donglai.web.constant.SessionConstant.SESSION_USERNAME;

@RestController
@Api(value = "BackOfficeUserController", tags = "后台用户管理")
@RequestMapping("/api/v1/backofficeUser")
@Slf4j
public class BackOfficeUserController {
    @Autowired
    BackOfficeUserProcessService backOfficeUserProcessService;
    @Autowired
    BackOfficeUserBackService backOfficeUserBackService;

    @ApiOperation(value = "登录")
    @PostMapping(value = "/login")
    //@BackLog(name = "登录到后台")
    public RestResponse login(LoginRequest loginRequest) {
        Object token = null;
        try {
            token = backOfficeUserProcessService.login(loginRequest);
            //存储属性到session中
            Session session = SecurityUtils.getSubject().getSession();
            session.setAttribute(SESSION_USERNAME, loginRequest.getUsername());
        }
        catch (DisabledAccountException e){
            return new ErrorResponse( GlobalResponseCode.ACCOUNT_DISABLED_ERROR);
        }
        catch (AuthenticationException e) {
            return new ErrorResponse( GlobalResponseCode.USERNAME_OR_PASSWORD_ERROR);
        }
        catch (Exception e) {
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

    @ApiOperation(value = "查询当前登录用户信息")
    @GetMapping(value = "/loginUserInfo")
    public RestResponse loginUserInfo(){
        Subject subject = SecurityUtils.getSubject();
        BackOfficeUser backOfficeUser = (BackOfficeUser) subject.getPrincipal();
        if(backOfficeUser==null) return new ErrorResponse(GlobalResponseCode.USER_NOT_FOUND);
        return new SuccessResponse().withData(ConvertUtils.backOfficeUserToBackOfficeUserVO(backOfficeUser));
    }

    @ApiOperation(value = "条件查询后台用户列表")
    @GetMapping("/userList")
    public RestResponse findList(BackOfficeUserFindListRequest request) {
        return new SuccessResponse().withData(backOfficeUserBackService.findList(request));
    }

    @ApiOperation(value = "后台用户 启用/关闭")
    @PostMapping("/enableOrClose")
    @BackLog(name = "启用/关闭后台用户 ")
    public RestResponse updateUserEnable(UpdateUserEnableRequest request) {
        return backOfficeUserBackService.updateUserEnable(request);
    }

    @ApiOperation(value = "后台用户 删除")
    @PostMapping("/delete")
    @BackLog(name = "删除后台账号")
    public RestResponse deleteBackUser(UpdateUserEnableRequest request) {
        return backOfficeUserBackService.deleteBackUser(request);
    }

    @ApiOperation(value = "后台用户 修改")
    @PostMapping("/update")
    @BackLog(name = "修改后台账号")
    public RestResponse updateBackOfficeUser(UpdateBackOfficeUserRequest request) {
        return backOfficeUserBackService.updateBackOfficeUser(request);
    }

    @ApiOperation(value = "后台用户 添加")
    @PostMapping("/add")
    @BackLog(name = "添加后台账号")
    public RestResponse addBackUser(AddBackUserRequest request) {
        return backOfficeUserBackService.addBackUser(request);
    }
    @ApiOperation(value = "修改密码")
    @PostMapping("/updatePassword")
    @BackLog(name = "修改后台账号密码")
    public RestResponse updatePassword(@ApiParam(value = "账号id") String id, @ApiParam(value = "新密码") String newPassword) {
        return backOfficeUserBackService.updatePassword(id, newPassword);
    }

}
