package com.donglai.live.message.services;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.live.process.RoomProcess;
import com.donglai.model.db.entity.live.Room;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.live.LiveUserService;
import com.donglai.model.db.service.live.RoomService;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.Live;
import com.donglai.protocol.message.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.donglai.live.util.ComparatorUtil.getRoomAudienceComparator;
import static com.donglai.live.util.MessageUtil.buildReply;

@Service("LiveOfGetLiveRoomListRequest")
public class LiveOfGetLiveRoomListRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    RoomService roomService;
    @Autowired
    UserService userService;
    @Autowired
    LiveUserService liveUserService;
    @Autowired
    RoomProcess roomProcess;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        Live.LiveOfGetLiveRoomListRequest request = message.getLiveOfGetLiveRoomListRequest();
        Live.LiveOfGetLiveRoomListReply.Builder replyBuilder = Live.LiveOfGetLiveRoomListReply.newBuilder();
        var roomIds = roomService.getAllLiveRoom();
        List<Room> allRooms = new ArrayList<>(roomIds.size());
        roomIds.forEach(roomId -> allRooms.add(roomService.findById(roomId)));
        var rooms = allRooms.stream().sorted(getRoomAudienceComparator()).map(Room::getId).collect(Collectors.toList());
        //var roomInfos = rooms.stream().map(room -> roomProcess.buildRoomInfo(room)).collect(Collectors.toList());
        replyBuilder.addAllRoomIds(rooms);
        //producer.sendReply(userId,replyBuilder.build(), Constant.ResultCode.SUCCESS);
        return buildReply(userId, replyBuilder.build(), Constant.ResultCode.SUCCESS);
    }

    @Override
    public void Close(String s) {

    }
}
