package com.donglaistd.jinli.processors.handler.race.landlord;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.game.landlord.Landlords;
import com.donglaistd.jinli.database.entity.game.landlord.PokerPlayer;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.util.DataManager;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class GrabLandlordRequestHandler extends MessageHandler {
    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.GrabLandlordRequest request = messageRequest.getGrabLandlordRequest();
        Jinli.GrabLandLordReply.Builder reply = Jinli.GrabLandLordReply.newBuilder();
        String gameId = request.getGameId();
        Landlords landlordGame = (Landlords) DataManager.findGame(gameId);
        if(Objects.isNull(landlordGame)) return buildReply(reply, Constant.ResultCode.GAME_NOT_EXISTS);
        PokerPlayer pokerPlayer = landlordGame.getPokerPlayerById(user.getId());
        if(Objects.isNull(pokerPlayer)) return buildReply(reply, Constant.ResultCode.NOT_GAME_MEMBER);
        boolean landlord = landlordGame.grabLandlord(pokerPlayer, request.getGrabLandlord());
        if(!landlord) return buildReply(reply, Constant.ResultCode.GAME_OPERATION_ILLEGAL);
        reply.setRate(landlordGame.getUserRate(user.getId()));
        return buildReply(reply, Constant.ResultCode.SUCCESS);
    }
}
