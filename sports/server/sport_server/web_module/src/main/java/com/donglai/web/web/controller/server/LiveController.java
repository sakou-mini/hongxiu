package com.donglai.web.web.controller.server;

import com.donglai.common.util.StringUtils;
import com.donglai.web.config.annotation.BackLog;
import com.donglai.web.process.LiveProcess;
import com.donglai.web.response.*;
import com.donglai.web.web.dto.reply.LiveDetailReply;
import com.donglai.web.web.dto.reply.LiveRecordReply;
import com.donglai.web.web.dto.request.LiveRecordRequest;
import com.donglai.web.web.dto.request.LiveRoomRequest;
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
@RequestMapping("/api/v1/live")
@Api(value = "LiveController", tags = "直播管理")
public class LiveController {
    @Autowired
    LiveProcess liveProcess;

    @ApiOperation("直播监控")
    @GetMapping("/liveMonitor")
    public RestResponse liveMonitor(LiveRoomRequest request){
        return new SuccessResponse().withData(liveProcess.queryLiveMonitor(request));
    }

    @ApiOperation("直播详情")
    @GetMapping("/liveDetail")
    public RestResponse liveDetail(@ApiParam(value = "直播间id") String roomId){
        if(StringUtils.isNullOrBlank(roomId)) return new ErrorResponse(GlobalResponseCode.PARAM_ERROR);
        LiveDetailReply liveDetail = liveProcess.getLiveDetail(roomId);
        if(Objects.isNull(liveDetail)) return new ErrorResponse(GlobalResponseCode.ROOM_NOT_LIVE);
        return new SuccessResponse().withData(liveDetail);
    }

    @ApiOperation("关闭直播")
    @PostMapping("/closeLive")
    @BackLog(name = "关闭直播")
    public RestResponse endLive(String roomId){
        GlobalResponseCode globalResponseCode = liveProcess.endLive(roomId);
        return globalResponseCode.equals(GlobalResponseCode.SUCCESS) ? new SuccessResponse() : new ErrorResponse(globalResponseCode);
    }

    @ApiOperation("直播记录")
    @PostMapping("/liveRecord")
    public RestResponse liveRecord(LiveRecordRequest request){
        PageInfo<LiveRecordReply> record = liveProcess.liveRecord(request);
        return new SuccessResponse().withData(record);
    }

}
