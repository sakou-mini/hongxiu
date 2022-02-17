package com.donglai.web.process;

import com.donglai.common.util.StringUtils;
import com.donglai.model.db.entity.live.LiveRecord;
import com.donglai.model.db.entity.live.Room;
import com.donglai.model.db.entity.sport.SportEvent;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.live.RoomService;
import com.donglai.model.db.service.sport.SportEventService;
import com.donglai.protocol.Live;
import com.donglai.web.db.server.service.LiveRecordDbService;
import com.donglai.web.db.server.service.RoomDbService;
import com.donglai.web.message.producer.Producer;
import com.donglai.web.response.GlobalResponseCode;
import com.donglai.web.response.PageInfo;
import com.donglai.web.util.LiveStreamUtil;
import com.donglai.web.web.dto.reply.LiveDetailReply;
import com.donglai.web.web.dto.reply.LiveRecordReply;
import com.donglai.web.web.dto.reply.LivingRoomReply;
import com.donglai.web.web.dto.request.LiveRecordRequest;
import com.donglai.web.web.dto.request.LiveRoomRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.donglai.web.response.GlobalResponseCode.*;

@Component
public class LiveProcess {
    @Autowired
    RoomService roomService;
    @Autowired
    RoomDbService roomDbService;
    @Autowired
    UserService userService;
    @Autowired
    Producer producer;
    @Autowired
    LiveRecordDbService liveRecordDbService;
    @Autowired
    SportEventService sportEventService;

    /*1.查询直播监控*/
    public PageInfo<LivingRoomReply> queryLiveMonitor(LiveRoomRequest request) {
        PageInfo<Room> pageResult = roomDbService.findLiveRoomByRequest(request);
        List<LivingRoomReply> content = pageResult.getContent().stream().map(this::buildLivingRoomReply).collect(Collectors.toList());
        return new PageInfo<>(pageResult,content);
    }

    public LivingRoomReply buildLivingRoomReply(Room room){
        return new LivingRoomReply(room, userService.findById(room.getUserId()));
    }

    /*关闭直播*/
    public GlobalResponseCode endLive(String roomId) {
        if(StringUtils.isNullOrBlank(roomId)) return PARAM_ERROR;
        Room room = roomService.findById(roomId);
        if (Objects.isNull(room) || !roomService.roomIsLive(roomId) || room.isClose() || !room.isLive()) {
            return ROOM_NOT_LIVE;
        }
        //发送消息通知直播服关闭直播间
        sendEndLiveRequest(room);
        return SUCCESS;
    }

    private void sendEndLiveRequest(Room room){
        Live.LiveOfEndLiveRequest.Builder request = Live.LiveOfEndLiveRequest.newBuilder().setEndDelayTime(5000);
        producer.sendMessageRequest(room.getUserId(),request.build());
    }

    public PageInfo<LiveRecordReply> liveRecord(LiveRecordRequest request) {
        PageInfo<LiveRecord> pageInfo = liveRecordDbService.liveRecordList(request);
        List<LiveRecordReply> replyContents = pageInfo.getContent().stream().map(this::buildLiveRecordReply).collect(Collectors.toList());
        return new PageInfo<>(pageInfo, replyContents);
    }


    private LiveRecordReply buildLiveRecordReply(LiveRecord liveRecord){
        LiveRecordReply liveRecordReply = new LiveRecordReply(userService.findById(liveRecord.getUserId()), liveRecord);
        if(!StringUtils.isNullOrBlank(liveRecord.getEventId())){
            SportEvent sportEvent = sportEventService.findByEventId(liveRecord.getEventId());
            if(Objects.nonNull(sportEvent)) liveRecordReply.setSportEvent(sportEvent);
        }
        return liveRecordReply;
    }

    public LiveDetailReply getLiveDetail(String roomId) {
        Room room = roomService.findById(roomId);
        LiveDetailReply liveDetailReply = null;
        if (Objects.isNull(room) || !roomService.roomIsLive(roomId) || room.isClose() || !room.isLive()) {
            return null;
        }else{
            liveDetailReply = new LiveDetailReply(room, userService.findById(room.getUserId()));
            liveDetailReply.setLiveUrl(LiveStreamUtil.getPullUrl(room));
            if(!StringUtils.isNullOrBlank(room.getEventId())){
                liveDetailReply.setEvent(sportEventService.findByEventId(room.getEventId()));
            }
        }
        return liveDetailReply;
    }
}
