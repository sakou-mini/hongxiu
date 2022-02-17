/*
package com.donglai.live.message.services.modultNotice;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.common.util.StringUtils;
import com.donglai.live.process.FollowProcess;
import com.donglai.live.util.LiveRedisUtil;
import com.donglai.model.db.entity.live.Room;
import com.donglai.model.db.service.common.FollowListService;
import com.donglai.model.db.service.live.RoomService;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("LiveOfUserFollowOrUnFollowRequest")
public class LiveOfUserFollowOrUnFollowRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    FollowListService followListService;
    @Autowired
    RoomService roomService;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getLiveOfUserFollowOrUnFollowRequest();
        String leaderId = request.getLeaderId();
        String followerUserId = request.getFollowerUserId();
        String roomId = LiveRedisUtil.getUserEnterRoomRecord(request.getLeaderId());
        if(!StringUtils.isNullOrBlank(roomId)) roomId = "";
        Room room = roomService.findById(roomId);
        FollowProcess.broadcastFollowUserInRoom(room, leaderId, followerUserId, request.getFollowType());
        return null;
    }

    @Override
    public void Close(String s) {

    }
}
*/
