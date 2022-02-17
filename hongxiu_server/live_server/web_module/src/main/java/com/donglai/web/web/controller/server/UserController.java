package com.donglai.web.web.controller.server;

import com.donglai.web.response.RestResponse;
import com.donglai.web.response.SuccessResponse;
import com.donglai.web.service.UserService;
import com.donglai.web.web.dto.request.AddUserRequest;
import com.donglai.web.web.dto.request.ResetUserRequest;
import com.donglai.web.web.dto.request.UpdateUserStatusRequest;
import com.donglai.web.web.dto.request.UserListRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "用户管理")
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/getUserList")
    @ApiOperation(value = "获取用户列表")
    public RestResponse getUserList(UserListRequest userListRequest) {
        return new SuccessResponse().withData(userService.getUserList(userListRequest));
    }

    /**
     * 修改用户状态
     *
     * @param updateUserStatusRequest 修改数据
     * @return 返回被修改数据
     */
    @PostMapping("/updateStatus")
    @ApiOperation(value = "修改用户状态")
    public RestResponse updateStatus(UpdateUserStatusRequest updateUserStatusRequest) {
        return new SuccessResponse().withData(userService.updateStatus(updateUserStatusRequest));
    }

    /**
     * 用户添加
     *
     * @param request 用户数据
     * @return 返回添加用户数据
     */
    @PostMapping("/addUser")
    @ApiOperation(value = "用户添加")
    public RestResponse addUser(AddUserRequest request) {
        return new SuccessResponse().withData(userService.addUser(request));
    }

    @PostMapping("/resetUser")
    @ApiOperation(value = "重置用户")
    public RestResponse resetUser(ResetUserRequest request) {
        return new SuccessResponse().withData(userService.resetUser(request));
    }


    @PostMapping("/getUserInfo")
    @ApiOperation(value = "用户详情")
    public RestResponse getUserInfo(String userId) {
        return new SuccessResponse().withData(userService.getUserInfo(userId));
    }
}

