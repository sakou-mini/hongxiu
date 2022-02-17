package com.donglaistd.jinli.processors.handler.race.goldenFlower;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.game.goldenflower.FriedGoldenFlower;
import com.donglaistd.jinli.database.entity.game.texas.RacePokerPlayer;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.util.DataManager;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class FlowerCompareCardsRequestHandler extends MessageHandler{
    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.FlowerCompareCardsReply.Builder reply = Jinli.FlowerCompareCardsReply.newBuilder();
        Jinli.FlowerCompareCardsRequest request = messageRequest.getFlowerCompareCardsRequest();
        FriedGoldenFlower goldenFlower = (FriedGoldenFlower) DataManager.findGame(request.getGameId());
        assert goldenFlower != null;
        RacePokerPlayer player = goldenFlower.getPlayerFromList(user.getId());
        if (Objects.isNull(player)) {
            return buildReply(reply, NOT_GAME_MEMBER);
        }
        RacePokerPlayer bySeatNum = goldenFlower.getPlayerBySeatNum(request.getSeatNum());
        if (Objects.nonNull(bySeatNum) && bySeatNum.isFold()) {
            return buildReply(reply, GAME_OPERATION_ILLEGAL);
        }
        goldenFlower.compareCards(player, request.getSeatNum());
        return buildReply(reply, SUCCESS);
    }
}
