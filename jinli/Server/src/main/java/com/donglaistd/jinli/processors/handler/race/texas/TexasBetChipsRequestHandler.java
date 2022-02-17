package com.donglaistd.jinli.processors.handler.race.texas;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.game.texas.Texas;
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
public class TexasBetChipsRequestHandler extends MessageHandler {
    private static final Logger logger = Logger.getLogger(TexasBetChipsRequestHandler.class.getName());

    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.TexasBetChipsReply.Builder reply = Jinli.TexasBetChipsReply.newBuilder();
        Jinli.TexasBetChipsRequest request = messageRequest.getTexasBetChipsRequest();
        Texas texas = (Texas) DataManager.findGame(request.getGameId());
        RacePokerPlayer player = texas.getPlayerFromList(user.getId());
        if (Objects.isNull(player)) return buildReply(reply, BET_FAILED);
        int chip = request.getChips();
        // 无效下注额,1筹码不足
        if (chip <= 0 || chip > player.getBringInChips()) {
            logger.warning("betChipIn error not enough chips:" + chip + " getBodyChips():" + player.getBringInChips());
            return buildReply(reply, INVALID_BET_AMOUNT);
        }
        texas.betChipIn(player, chip, true);
        return buildReply(reply, SUCCESS);
    }

}
