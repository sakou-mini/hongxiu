package com.donglaistd.jinli.processors.handler.redpacket;


import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.LiveUserDaoService;
import com.donglaistd.jinli.database.dao.RedPacketRecordDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.RedPacket;
import com.donglaistd.jinli.database.entity.RedPacketRecord;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.util.DataManager;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.donglaistd.jinli.Constant.ResultCode.REDPACKET_NOT_EXISTS;
import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;
/**
 * @deprecated
 */
@Component
public class QueryRedPacketInfoRequestHandler extends MessageHandler {
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    RedPacketRecordDaoService redPacketRecordDaoService;
    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.QueryRedPacketInfoRequest request = messageRequest.getQueryRedPacketInfoRequest();
        Jinli.QueryRedPacketInfoReply.Builder reply = Jinli.QueryRedPacketInfoReply.newBuilder();
       /* String redPacketId = request.getRedPacketId();
        Room room = DataManager.getRoomFromChannel(ctx);
        if(Objects.isNull(room))
            room = DataManager.closeRoomInfo.get(ctx.channel().attr(ROOM_KEY).get());
        if(Objects.isNull(room)){
            return buildReply(reply,REDPACKET_NOT_EXISTS);
        }
        RedPacket redPacket = room.getRedPacket(redPacketId);;
        if(Objects.isNull(redPacket))   return buildReply(reply,REDPACKET_NOT_EXISTS);
        User owner = userDaoService.findById(redPacket.getUserId());
        RedPacketRecord record = redPacketRecordDaoService.findRedPacketRecordByUserIdAndRedPacketId(user.getId(), redPacketId);
        if(Objects.nonNull(record)) reply.setGetCoin(record.getAmount());
        reply.setRedPacketInfo(redPacket.toProto(user.getId())).setUserInfo(owner.toSummaryProto());*/
        return buildReply(reply,SUCCESS);
    }
}
