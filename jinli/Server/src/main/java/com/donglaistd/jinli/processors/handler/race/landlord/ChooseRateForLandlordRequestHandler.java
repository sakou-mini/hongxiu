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
public class ChooseRateForLandlordRequestHandler extends MessageHandler {

    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.ChooseRateForLandlordRequest request = messageRequest.getChooseRateForLandlordRequest();
        Jinli.ChooseRateForLandlordReply.Builder reply = Jinli.ChooseRateForLandlordReply.newBuilder();
        Landlords landlordGame = (Landlords) DataManager.findGame(request.getGameId());
        if(Objects.isNull(landlordGame)) return buildReply(reply, Constant.ResultCode.GAME_NOT_EXISTS);
        PokerPlayer pokerPlayer = landlordGame.getPokerPlayerById(user.getId());
        if(Objects.isNull(pokerPlayer)) return buildReply(reply, Constant.ResultCode.NOT_GAME_MEMBER);
        boolean result = landlordGame.plusRate(pokerPlayer, request.getChooseLandlord());
        if(!result) return buildReply(reply, Constant.ResultCode.GAME_OPERATION_ILLEGAL);
        reply.setGameId(landlordGame.getGameId()).setRate(landlordGame.getUserRate(user.getId()));
        return buildReply(reply, Constant.ResultCode.SUCCESS);
    }
}
