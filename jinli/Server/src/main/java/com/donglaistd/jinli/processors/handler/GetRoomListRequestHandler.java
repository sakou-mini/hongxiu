package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.service.RoomProcess;
import com.donglaistd.jinli.util.ComparatorUtil;
import com.donglaistd.jinli.util.DataManager;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class GetRoomListRequestHandler extends MessageHandler {
    private static final Logger logger = Logger.getLogger(GetRoomListRequestHandler.class.getName());
    @Autowired
    RoomProcess roomProcess;

    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.GetRoomListRequest request = messageRequest.getGetRoomListRequest();
        int maxCount = request.getMaxCount();
        List<Room> roomList = filterPlatformRoom(maxCount,user);
        roomList = roomList.stream().sorted(ComparatorUtil.getRoomRecommendComparator(user.getPlatformType())).collect(Collectors.toList());;
        List<Jinli.RoomInfo> livingRoomInfoList = new ArrayList<>();
        Jinli.RoomInfo roomInfo;
        for (Room room : roomList) {
            roomInfo = roomProcess.buildLivingRoomInfo(room);
            if(Objects.nonNull(roomInfo)) livingRoomInfoList.add(roomInfo);
        }
        Jinli.GetRoomListReply.Builder reply = Jinli.GetRoomListReply.newBuilder().addAllRoomList(livingRoomInfoList);
        return buildReply(reply, Constant.ResultCode.SUCCESS);
    }

    public List<Room> filterPlatformRoom(int maxCount, User requestUser){
        Constant.PlatformType platformType = requestUser.getPlatformType();
        return DataManager.roomMap.values().stream().filter(room -> room.isLive() && room.isSharedToPlatform(platformType)).limit(maxCount).collect(Collectors.toList());
    }
}