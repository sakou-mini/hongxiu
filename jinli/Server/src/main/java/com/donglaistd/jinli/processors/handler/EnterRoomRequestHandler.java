package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.annotation.IgnoreShutDown;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.service.RoomProcess;
import com.donglaistd.jinli.util.DataManager;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.logging.Logger;

import static com.donglaistd.jinli.util.DataManager.saveRoomKeyToChannel;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@IgnoreShutDown
@Component
public class EnterRoomRequestHandler extends MessageHandler {
    private final Logger logger = Logger.getLogger(EnterRoomRequestHandler.class.getName());
    @Autowired
    RoomProcess roomProcess;
    @Autowired
    DataManager dataManager;

    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.EnterRoomRequest request = messageRequest.getEnterRoomRequest();
        Jinli.EnterRoomReply.Builder reply = Jinli.EnterRoomReply.newBuilder();
        logger.info("receieve enterRoom message: "+ request);

        var room = DataManager.findOnlineRoom(request.getRoomId());
        if (Objects.isNull(room) || !room.isLive()) {
            logger.warning("enterRoom roomInfo is " + room);
            resultCode = Constant.ResultCode.ROOM_DOES_NOT_EXIST;
        } else {
            resultCode = Constant.ResultCode.SUCCESS;
            roomProcess.quitIfHasEnterOtherRoom(ctx,user);
            saveRoomKeyToChannel(ctx, room.getId());
            reply = roomProcess.dealEnterRoom(room, user).toBuilder();
        }
        return buildReply(reply, resultCode);
    }
}

