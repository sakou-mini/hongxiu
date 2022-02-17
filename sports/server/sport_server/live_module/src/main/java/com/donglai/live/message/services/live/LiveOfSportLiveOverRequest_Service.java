package com.donglai.live.message.services.live;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.live.message.producer.Producer;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.live.LiveUser;
import com.donglai.model.db.entity.live.Room;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.live.LiveUserService;
import com.donglai.model.db.service.live.RoomService;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.Live;
import com.donglai.protocol.message.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("LiveOfSportLiveOverRequest")
@Slf4j
public class LiveOfSportLiveOverRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    UserService userService;
    @Autowired
    RoomService roomService;
    @Autowired
    LiveUserService liveUserService;
    @Autowired
    Producer producer;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getLiveOfSportLiveOverRequest();
        log.info("直播赛事已结束");
        User user = userService.findById(userId);
        LiveUser liveUser = liveUserService.findById(request.getLiveUserId());
        //FIXME 获取当前正在开播的房间，通知 给主播赛事已经结束  ,以后可能还需要记录流水
        if(roomService.roomIsLive(liveUser.getRoomId())){
            //存储记录
            Room room = roomService.findById(liveUser.getRoomId());
            room.setEventGiftIncome(request.getGiftIncome());
            room.setEventPopularity(request.getPopularity());
            roomService.save(room);
            var broadCastMessage = Live.LiveOfSportLiveOverBroadcastMessage.newBuilder().setEventGiftIncome(request.getGiftIncome()).setEventPopularity(request.getPopularity());
            producer.sendReply(userId,broadCastMessage.build(), Constant.ResultCode.SUCCESS);
        }
        return null;
    }

    @Override
    public void Close(String s) {

    }
}
