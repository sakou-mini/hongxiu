package com.donglai.web.web.controller.server;

import com.donglai.model.db.entity.sport.SportEvent;
import com.donglai.web.process.SportEventProcess;
import com.donglai.web.response.*;
import com.donglai.web.web.dto.reply.SportEventInfoReply;
import com.donglai.web.web.dto.reply.SportEventRecordReply;
import com.donglai.web.web.dto.request.HistoryEventRequest;
import com.donglai.web.web.dto.request.SettingEventLiveRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sportEvent")
@Api(value = "SportEventController", tags = "赛事管理")
public class SportEventController {
    @Autowired
    SportEventProcess sportEventProcess;

    @ApiOperation(value = "当前赛事列表")
    @GetMapping("/local")
    public RestResponse localEvent(@ApiParam(name = "分页") int page, @ApiParam(name = "分页大小") int size){
        PageInfo<SportEventInfoReply> replyPageInfo = sportEventProcess.queryLocalEvent(page, size);
        return new SuccessResponse().withData(replyPageInfo);
    }

    @ApiOperation(value = "历史赛事列表")
    @GetMapping("/history")
    public RestResponse historyEvent(HistoryEventRequest request){
        PageInfo<SportEventRecordReply> eventRecord = sportEventProcess.getEventRecord(request);
        return new SuccessResponse().withData(eventRecord);
    }

    @ApiOperation(value = "配置赛事直播")
    @PostMapping("/settingEvent")
    public RestResponse settingEvent(SettingEventLiveRequest request){
        GlobalResponseCode globalResponseCode = sportEventProcess.settingEventLive(request);
        return globalResponseCode.equals(GlobalResponseCode.SUCCESS) ? new SuccessResponse() : new ErrorResponse(globalResponseCode);
    }

    @ApiOperation(value = "模拟赛事")
    @PostMapping("/mockEvent")
    public RestResponse mockEvent(String name, long time,String homeTeam,String awayTeam,String competition,String sport){
        SportEvent sportEvent = sportEventProcess.mockEvent(name, time, homeTeam, awayTeam, competition, sport);
        return new SuccessResponse().withData(sportEvent);
    }
}
