package com.donglai.live.message.services.modultNotice;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.common.util.CastUtil;
import com.donglai.live.message.producer.Producer;
import com.donglai.live.process.LoginProcess;
import com.donglai.live.process.RoomProcess;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.live.Room;
import com.donglai.model.db.service.common.UserService;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.Live;
import com.donglai.protocol.message.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service("LiveOfUserConnectionRequest")
@Slf4j
public class LiveOfUserConnectionRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    UserService userService;
    @Autowired
    LoginProcess loginProcess;
    @Autowired
    RoomProcess roomProcess;
    @Autowired
    Producer producer;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        Map<KafkaMessage.ExtraParam, String> extraParam = CastUtil.cast(param[0]);
        Live.LiveOfUserConnectionRequest request = message.getLiveOfUserConnectionRequest();
        userId = request.getUserId();
        User user = userService.findById(userId);
        if (Objects.isNull(user)) return null;
        //1.删除自动关闭队列
        loginProcess.cancelEndLiveQueueIfPresent(user);
        //2.广播恢复直播间
        Room room = loginProcess.recoverEnterRoomIfPresent(user);
        if (Objects.nonNull(room)) {
            log.info("恢复用户的直播间{}",room.getUserId());
            broadCastRecoverLiveRoomMessage(room, userId, extraParam);
        }
        return null;
    }

    public void broadCastRecoverLiveRoomMessage(Room room, String userId, Map<KafkaMessage.ExtraParam, String> param) {
        var message = Live.LiveOfRecoverLiveRoomBroadcastMessage.newBuilder().setReconnectedRoomInfo(roomProcess.buildRoomInfo(room));
        producer.sendReply(userId, message.build(), Constant.ResultCode.SUCCESS, param);
    }

    @Override
    public void Close(String s) {

    }
}
