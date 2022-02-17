package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class SendCommonBroadcastMessageRequestHandler extends MessageHandler{
    @Autowired
    DataManager dataManager;

    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.SendCommonBroadcastMessageRequest request = messageRequest.getSendCommonBroadcastMessageRequest();
        Jinli.SendCommonBroadcastMessageReply.Builder replyBuilder = Jinli.SendCommonBroadcastMessageReply.newBuilder();
        if(StringUtils.isNullOrBlank(user.getLiveUserId())) return buildReply(replyBuilder, Constant.ResultCode.NOT_LIVE_USER);
        Room room = DataManager.findRoomByLiveUserId(user.getLiveUserId());
        if(room == null) return buildReply(replyBuilder, Constant.ResultCode.ROOM_DOES_NOT_EXIST);
        Jinli.CommonBroadcastMessage.Builder broadcastMessage = Jinli.CommonBroadcastMessage.newBuilder().setBroadcastType(request.getBroadcastType());
        if(!StringUtils.isNullOrBlank(request.getContent())) broadcastMessage.setContent(request.getContent());
        room.broadCastToAllPlatform(buildReply(broadcastMessage));
        return buildReply(replyBuilder, Constant.ResultCode.SUCCESS);
    }
}
