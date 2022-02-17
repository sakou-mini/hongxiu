package com.donglaistd.jinli.processors.handler.redpacket;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.RedPacket;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.util.DataManager;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.donglaistd.jinli.util.MessageUtil.buildReply;
/**
 * @deprecated
 */
@Component
public class QueryRoomRedPacketSummaryRequestHandler extends MessageHandler {
    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.QueryRoomRedPacketSummaryRequest request = messageRequest.getQueryRoomRedPacketSummaryRequest();
        Jinli.QueryRoomRedPacketSummaryReply.Builder reply = Jinli.QueryRoomRedPacketSummaryReply.newBuilder();
        /*String roomId = request.getRoomId();
        Room room = DataManager.findOnlineRoom(roomId);
        if(Objects.isNull(room))  return buildReply(reply, Constant.ResultCode.ROOM_DOES_NOT_EXIST);
        RedPacket redPacket = room.getFirstRedPacketQueue();
        if(Objects.nonNull(redPacket))
            reply.setNextOpenTime(redPacket.getOpenTime()).setRedPacketId(redPacket.getId()).setCountDownTime(redPacket.getCountDownTime());
        reply.setRedPacketNum(room.getRedPacketQueue().size());*/
        return buildReply(reply, Constant.ResultCode.SUCCESS);
    }
}
