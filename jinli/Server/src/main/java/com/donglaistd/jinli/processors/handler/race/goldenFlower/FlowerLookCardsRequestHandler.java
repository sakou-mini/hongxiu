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

import static com.donglaistd.jinli.Constant.ResultCode.NOT_GAME_MEMBER;
import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;
import static com.donglaistd.jinli.util.MessageUtil.getJinliCard;

@Component
public class FlowerLookCardsRequestHandler extends MessageHandler {
    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.FlowerLookCardsReply.Builder reply = Jinli.FlowerLookCardsReply.newBuilder();
        Jinli.FlowerLookCardsRequest request = messageRequest.getFlowerLookCardsRequest();
        FriedGoldenFlower goldenFlower = (FriedGoldenFlower) DataManager.findGame(request.getGameId());
        assert goldenFlower != null;
        RacePokerPlayer player = goldenFlower.getPlayerFromList(user.getId());

        if (Objects.isNull(player)) {
            return buildReply(reply, NOT_GAME_MEMBER);
        }
        goldenFlower.lookCards(player);
        reply.addAllHandPokers(getJinliCard(player.getHandPokers()));
        return buildReply(reply, SUCCESS);
    }
}
