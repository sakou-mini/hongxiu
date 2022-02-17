package com.donglai.web.web.controller.server;

import com.donglai.model.db.entity.common.User;
import com.donglai.web.config.annotation.BackLog;
import com.donglai.web.process.LiveUserProcess;
import com.donglai.web.response.ErrorResponse;
import com.donglai.web.response.GlobalResponseCode;
import com.donglai.web.response.RestResponse;
import com.donglai.web.response.SuccessResponse;
import com.donglai.web.web.dto.reply.LiveUserDetailDto;
import com.donglai.web.web.dto.request.LiveUserListRequest;
import com.donglai.web.web.dto.request.UpdateUserStatusRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/liveUser")
@Api(value = "LiveUserController", tags = "主播API")
public class LiveUserController {
    @Autowired
    LiveUserProcess liveUserProcess;

    @ApiOperation("主播列表")
    @GetMapping("/liveUserList")
    public RestResponse liveUserList(LiveUserListRequest request) {
        return new SuccessResponse().withData(liveUserProcess.liveUserList(request));
    }

    @ApiOperation("主播详情")
    @GetMapping("/liveUserDetail")
    public RestResponse liveUserDetail(@ApiParam(value = "用户Id")  String userId){
        LiveUserDetailDto liveUserDetail = liveUserProcess.getLiveUserDetail(userId);
        if(Objects.isNull(liveUserDetail)) return new ErrorResponse(GlobalResponseCode.USER_NOT_FOUND);
        return new SuccessResponse().withData(liveUserDetail);
    }

    @ApiOperation("修改状态")
    @PostMapping("/updateStatus")
    @BackLog(name = "修改主播状态")
    public RestResponse updateStatus(UpdateUserStatusRequest request){
        GlobalResponseCode globalResponseCode = liveUserProcess.updateUserStatus(request);
        return globalResponseCode.equals(GlobalResponseCode.SUCCESS) ? new SuccessResponse() : new ErrorResponse(globalResponseCode);
    }

    @ApiOperation("修改密码")
    @PostMapping("/updatePassword")
    @BackLog(name = "修改主播密码")
    public RestResponse updatePassword(String userId,String password){
        GlobalResponseCode globalResponseCode = liveUserProcess.updatePassword(userId,password);
        return globalResponseCode.equals(GlobalResponseCode.SUCCESS) ? new SuccessResponse() : new ErrorResponse(globalResponseCode);
    }

    @ApiOperation("按id查询主播信息")
    @PostMapping("/userInfo")
    public RestResponse updatePassword(@ApiParam(value = "用户id", example = "0") String userId){
        return liveUserProcess.userDetailById(userId);
    }

}
