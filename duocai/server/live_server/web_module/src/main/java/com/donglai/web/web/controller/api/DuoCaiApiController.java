package com.donglai.web.web.controller.api;

import com.donglai.common.util.PasswordUtil;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.other.PlatformToken;
import com.donglai.web.process.PlatformApiProcess;
import com.donglai.web.response.ErrorResponse;
import com.donglai.web.response.RestResponse;
import com.donglai.web.response.SuccessResponse;
import com.donglai.web.util.IpUtil;
import com.donglai.web.web.dto.reply.PlatformRoomListReply;
import com.google.common.base.Objects;
import io.netty.util.internal.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static com.donglai.web.response.GlobalResponseCode.*;

@RestController
@RequestMapping("/api/v1/duoCaiApi")
@Api(value = "DuoCaiApiController", tags = "平台方api接口")
public class DuoCaiApiController {
    @Autowired
    PlatformApiProcess platformApiProcess;

    @PostMapping("/requestToken")
    public RestResponse requestToken(HttpServletRequest request) {
        //platformApiProcess.initPlatformUserByUserId(accountId);
        PlatformToken tokenResult = platformApiProcess.generatedToken(IpUtil.getIP(request));
        return new SuccessResponse().withData(tokenResult);
    }

    @GetMapping("/roomList")
    public RestResponse roomList(@ApiParam(value = "账号token信息") String token) {
        PlatformToken tokenResult = platformApiProcess.getTokenResultByToken(token);
        if (tokenResult == null) return new ErrorResponse(TOKEN_EXPIRE);
        PlatformRoomListReply roomListReply = platformApiProcess.getPlatformRoomListReply(tokenResult);
        if (roomListReply.getLiveRoomInfo().isEmpty())
            return new SuccessResponse("room is empty").withData(roomListReply);
        return new SuccessResponse().withData(roomListReply);
    }

    @PostMapping("/applyLiveUser")
    public RestResponse applyLiveUser(HttpServletRequest request, @ApiParam(value = "申请账号") String account) {
        String header = request.getHeader("Auth-Key");
        if (!Objects.equal("6c4c8376-3ac9-4092-8c40-5c15f40ca6b3", header))
            return new ErrorResponse(TOKEN_ERROR);
        if (StringUtil.isNullOrEmpty(account)) {
            return new ErrorResponse(PARAM_ERROR);
        }
        User user = platformApiProcess.initDuoCaiUserAndBecomeLiveUser(account);
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("account", account);
        userInfo.put("password", PasswordUtil.decodePassword(user.getPassword()));
        return new SuccessResponse().withData(userInfo);
    }
}
