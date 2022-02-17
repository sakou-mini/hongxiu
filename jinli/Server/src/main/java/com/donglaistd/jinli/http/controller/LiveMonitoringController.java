package com.donglaistd.jinli.http.controller;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.MuteProperty;
import com.donglaistd.jinli.http.entity.HttpURLConnection;
import com.donglaistd.jinli.http.entity.PageInfo;
import com.donglaistd.jinli.http.entity.live.ChatMessage;
import com.donglaistd.jinli.http.entity.live.RoomAudience;
import com.donglaistd.jinli.service.statistic.LiveMonitorProcess;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.VerifyUtil;
import com.google.common.collect.Lists;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import static com.donglaistd.jinli.constant.GameConstant.EMPTY_GAME_CLOSE_DELAY_TIME;

@RestController
@RequestMapping("backOffice/liveMonitoring")
public class LiveMonitoringController {
    final LiveMonitorProcess liveMonitorProcess;
    final VerifyUtil verifyUtil;
    final DataManager dataManager;
    private final Logger logger = Logger.getLogger(LiveMonitoringController.class.getName());


    public LiveMonitoringController(LiveMonitorProcess liveMonitorProcess, VerifyUtil verifyUtil, DataManager dataManager) {
        this.liveMonitorProcess = liveMonitorProcess;
        this.verifyUtil = verifyUtil;
        this.dataManager = dataManager;
    }

    @RequestMapping("/chatMessage")
    public List<ChatMessage> roomChatHistory(String roomId){
        return liveMonitorProcess.queryChatHistory(roomId);
    }

    @RequestMapping("/audienceList")
    public PageInfo<RoomAudience> audienceList(String roomId, String userId, String ip ,int page,int size){
        return liveMonitorProcess.queryRoomAudienceList(roomId,userId,ip,page,size);
    }

    @RequestMapping("/muteChatRequest")
    public HttpURLConnection<Object> muteChat(String roomId, String idsStr, MuteProperty muteProperty){
        if (muteProperty.getMuteReason() == null || muteProperty.getMuteTimeType() == null || muteProperty.getMuteArea() == null
                || Objects.equals(Constant.MuteArea.MUTE_AREA_DEFAULT, muteProperty.getMuteArea())) {
            return new HttpURLConnection<>(500, "missing mute param");
        }
        Constant.ResultCode resultCode = liveMonitorProcess.muteRoomChat(roomId, Lists.newArrayList(idsStr.split(";")),muteProperty);
        if (!Objects.equals(Constant.ResultCode.SUCCESS, resultCode)){
            logger.warning("muteChat failed! reason is:" + resultCode);
        }
        return new HttpURLConnection<>(200,resultCode.name());
    }

    @RequestMapping("/unMuteChat")
    public HttpURLConnection<Object> unMuteChat(String roomId,String userId){
        liveMonitorProcess.unMuteAllChat(roomId,userId);
        return new HttpURLConnection<>(200, Constant.ResultCode.SUCCESS.toString().toLowerCase());
    }

    @RequestMapping("/closeLive")
    public HttpURLConnection<Object> closeLiveRoom(String liveUserId, Principal principal) {
        if(!verifyUtil.checkLiveIsExist(liveUserId)){
          return new HttpURLConnection<>(500, "roomNotExit");
        }
        Constant.ResultCode resultCode = liveMonitorProcess.closeLiveRoom(liveUserId,principal.getName(),EMPTY_GAME_CLOSE_DELAY_TIME, Constant.EndType.NORMAL_END);
        int code  = Objects.equals(Constant.ResultCode.SUCCESS, resultCode) ? 200 : 500;
        return new HttpURLConnection<>(code, resultCode.name());
     }
}
