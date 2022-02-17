package com.donglaistd.jinli.processors.handler.redpacket;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.builder.RedPacketBuilder;
import com.donglaistd.jinli.config.RedPacketProperties;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.event.ModifyCoinEvent;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.util.DataManager;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;
/**
 * @deprecated
 */
@Component
public class CreateRedPacketRequestHandler extends MessageHandler {
    @Autowired
    RedPacketBuilder redPacketBuilder;
    @Autowired
    RedPacketProperties redPacketProperties;
    @Autowired
    DataManager dataManager;

    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.CreateRedPacketRequest request = messageRequest.getCreateRedPacketRequest();
        Jinli.CreateRedPacketReply.Builder reply = Jinli.CreateRedPacketReply.newBuilder();
        if (!redPacketProperties.isEnable()) return buildReply(reply, OPERATION_NOTALLOW);

        /*var room = DataManager.getRoomFromChannel(ctx);
        var resultCode = verifyCreateRedPacket(request, user, room);
        if (!Objects.equals(SUCCESS, resultCode)) return buildReply(reply, resultCode);
        var redPacket = redPacketBuilder.create(user.getId(), request.getCoinAmount(), request.getNum());
        EventPublisher.publish(new ModifyCoinEvent(user.getId(), -request.getCoinAmount()));
        room.addRedPacket(redPacket);
        if (room.getRedPacketQueue().size() > 1) {
            room.broadCasteRedInfoMessage(room.getFirstRedPacketQueue());
        }
        dataManager.saveRoom(room);
        return buildReply(reply.setTotalCoin(user.getGameCoin() - request.getCoinAmount()), resultCode);*/
        return buildReply(reply);
    }

    private boolean verifyRedPacketParam(int coinAmount, int totalNum) {
        if (coinAmount < redPacketProperties.getMinCoin() || coinAmount > redPacketProperties.getMaxCoin())
            return false;
        return totalNum >= redPacketProperties.getMinNum() && totalNum <= redPacketProperties.getMaxNum();
    }

    private Constant.ResultCode verifyCreateRedPacket(Jinli.CreateRedPacketRequest request, User user, Room room) {
        Constant.ResultCode resultCode;
        if (!verifyRedPacketParam(request.getCoinAmount(), request.getNum()))
            resultCode = REDPACKET_PARAM_ERROR;
        else if (Objects.isNull(user) || user.getGameCoin() < request.getCoinAmount())
            resultCode = NOT_ENOUGH_GAMECOIN;
        else if (Objects.isNull(room))
            resultCode = ROOM_DOES_NOT_EXIST;
        else resultCode = SUCCESS;
        return resultCode;
    }
}
