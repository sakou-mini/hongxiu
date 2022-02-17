package com.donglai.live.message.services;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.live.process.LiveProcess;
import com.donglai.model.db.entity.sport.SportEvent;
import com.donglai.model.db.service.sport.SportEventService;
import com.donglai.model.db.service.sport.SportLiveScheduleService;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.Live;
import com.donglai.protocol.message.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.donglai.live.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.SUCCESS;

@Service("LiveOfQueryLiveUserSportEventListRequest")
public class LiveOfQueryLiveUserSportEventListRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    SportLiveScheduleService sportLiveScheduleService;
    @Autowired
    SportEventService sportEventService;
    @Autowired
    LiveProcess liveProcess;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var replyBuilder = Live.LiveOfQueryLiveUserSportEventListReply.newBuilder();
        var sportEventInfoList = sportLiveScheduleService.findByUserId(userId);
        //筛选出可用的赛事
        sportEventInfoList = sportEventInfoList.stream().filter(liveScheduling -> Objects.equals(SUCCESS, liveProcess.verifySportEventLive(liveScheduling))).collect(Collectors.toList());
        //封装赛事
        List<Live.SportEventInfo> eventInfos = sportEventInfoList.stream().map(sportLiveScheduling -> sportEventService.findByEventId(sportLiveScheduling.getEventId()))
                .filter(Objects::nonNull).map(SportEvent::toProto).collect(Collectors.toList());
        replyBuilder.addAllSportEventInfos(eventInfos);
        return buildReply(userId, replyBuilder.build(), SUCCESS);
    }

    @Override
    public void Close(String s) {

    }
}
