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
import java.util.logging.Logger;

import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class FlowerBetChipsRequestHandler extends MessageHandler {
    private static final Logger logger = Logger.getLogger(FlowerBetChipsRequestHandler.class.getName());

    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.FlowerBetChipsReply.Builder reply = Jinli.FlowerBetChipsReply.newBuilder();
        Jinli.FlowerBetChipsRequest request = messageRequest.getFlowerBetChipsRequest();
        FriedGoldenFlower goldenFlower = (FriedGoldenFlower) DataManager.findGame(request.getGameId());
        assert goldenFlower != null;
        RacePokerPlayer player = goldenFlower.getPlayerFromList(user.getId());
        if (Objects.isNull(player)) {
            return buildReply(reply, BET_FAILED);
        }
        int chip = request.getChips();
        // 无效下注额,1筹码不足
        if (chip <= 0 || chip > player.getBringInChips()) {
            logger.warning("betChipIn error not enough chips:" + chip + " getBodyChips():" + player.getBringInChips());
            return buildReply(reply, INVALID_BET_AMOUNT);
        }
        if (player.isLook()) {
            if (chip > goldenFlower.getConfig().getMaxBright()) {
                buildReply(reply, GAME_OPERATION_ILLEGAL);
            }
        } else {
            if (chip > goldenFlower.getConfig().getMaxDark()) {
                buildReply(reply, GAME_OPERATION_ILLEGAL);
            }
        }
        goldenFlower.betChipIn(player, request.getChips(), true);
        return buildReply(reply, SUCCESS);
    }
}
