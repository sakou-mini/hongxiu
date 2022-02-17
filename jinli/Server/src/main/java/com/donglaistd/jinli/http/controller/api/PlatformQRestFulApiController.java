package com.donglaistd.jinli.http.controller.api;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.http.entity.PlatformQRoomInfo;
import com.donglaistd.jinli.http.entity.RoomListResultInfo;
import com.donglaistd.jinli.http.entity.TokenRequestResult;
import com.donglaistd.jinli.service.api.PlatformQApiService;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

import static com.donglaistd.jinli.constant.GameConstant.*;
import static com.donglaistd.jinli.constant.PlatformConstant.Q_PLATFORM_H5_LOAD_PATH;

@RestController
@RequestMapping("/api/v2")
public class PlatformQRestFulApiController {
    @Autowired
    PlatformQApiService platformQApiService;

    @RequestMapping(value = "/requestToken")
    public TokenRequestResult requestToken(String accountName, String displayName, String userAvatar, boolean mute, Principal principal) {
        String platformName = principal.getName();
        if (Strings.isNullOrEmpty(accountName)) return new TokenRequestResult(RESTFUL_RESULT_MISSING_PARAMETER, "missing parameter");

        int resultCode = platformQApiService.initPlatformUser(accountName, displayName, userAvatar, platformName,mute);
        if(!Objects.equals(RESTFUL_RESULT_SUCCESS,resultCode)) return new TokenRequestResult(resultCode, "account displayName exist");
        return platformQApiService.getTokenResult(accountName, platformName);
    }

    @RequestMapping(value = "/requestRoomList")
    public RoomListResultInfo requestRoomList(String token) {
        TokenRequestResult tokenResult = platformQApiService.getTokenResultByToken(token);
        if (tokenResult == null) {
            return new RoomListResultInfo(RESTFUL_RESULT_TOKEN_EXPIRE, "token not exit or expire");
        }
        List<PlatformQRoomInfo> roomList = platformQApiService.findPlatformQLiveRoom(tokenResult);
        List<String> qPlatformLines = platformQApiService.getQPlatformLines();
        RoomListResultInfo resultInfo = new RoomListResultInfo(RESTFUL_RESULT_SUCCESS, "success", roomList, qPlatformLines);
        resultInfo.setLoadUrl(Q_PLATFORM_H5_LOAD_PATH+"&webMobileType=" + Constant.WebMobileType.WEBMOBILE_PLATFORM_Q.name());
        return resultInfo;
    }
}
