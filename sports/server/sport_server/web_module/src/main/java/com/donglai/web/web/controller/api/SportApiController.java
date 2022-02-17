package com.donglai.web.web.controller.api;

import com.donglai.web.process.PlatformApiProcess;
import com.donglai.web.response.ErrorResponse;
import com.donglai.web.response.RestResponse;
import com.donglai.web.response.SuccessResponse;
import com.donglai.web.util.IpUtil;
import com.donglai.web.web.dto.reply.SportLiveReply;
import com.google.common.base.Objects;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static com.donglai.web.constant.WebConstant.SPORT_AUTH_KEY;
import static com.donglai.web.response.GlobalResponseCode.*;

@RequestMapping("/api/v1/sportApi")
@RestController
@Api(value = "SportApiController", tags = "体育赛事api接口")
public class SportApiController {

    @Autowired
    PlatformApiProcess platformApiProcess;


    @PostMapping("/liveList")
    public RestResponse sportLiveList(HttpServletRequest request, @ApiParam(value = "赛事id") String eventId){
        if (!PlatformApiProcess.verifyHeaderToken(request)) return new ErrorResponse(TOKEN_ERROR);
        // 根据赛事id 查询 直播安排  -> 根据直播排班 -> 查询直播相关信息
        SportLiveReply sportLiveReply = platformApiProcess.queryLiveEventByEventId(eventId, IpUtil.getIP(request));
        if(CollectionUtils.isEmpty(sportLiveReply.getLiveRaceInfo()))
            return new ErrorResponse(NOT_EXIT_RACE_LIVE);
        return new SuccessResponse().withData(sportLiveReply);
    }

    /*获取所有开播的赛事信息*/
    @PostMapping("/raceList")
    public RestResponse allRaceList(HttpServletRequest request) {
        if (!PlatformApiProcess.verifyHeaderToken(request)) return new ErrorResponse(TOKEN_ERROR);
        SportLiveReply sportLiveReply = platformApiProcess.getAllEventLive(IpUtil.getIP(request));
        if(CollectionUtils.isEmpty(sportLiveReply.getLiveRaceInfo()))
            return new ErrorResponse(NOT_EXIT_RACE_LIVE);
        return new SuccessResponse().withData(sportLiveReply);
    }

    @PostMapping("/raceOver")
    public RestResponse raceOver(HttpServletRequest request , @ApiParam(value = "主播id")  String liveUserId, @ApiParam(value = "礼物收入") int giftIncome, @ApiParam(value = "人气值") int popularity){
        if (!PlatformApiProcess.verifyHeaderToken(request)) return new ErrorResponse(TOKEN_ERROR);
        boolean result = platformApiProcess.raceOver(liveUserId, giftIncome, popularity);
        if(result){
            return new SuccessResponse();
        }else
            return new ErrorResponse(LIVE_USER_NOT_EXIT);
    }
}
