package com.donglaistd.jinli.processors.handler.race.texas;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.game.texas.Texas;
import com.donglaistd.jinli.database.entity.game.texas.RacePokerPlayer;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.util.DataManager;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

import static com.donglaistd.jinli.Constant.GameStatus.BETTING;
import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class TexasCheckRequestHandler extends MessageHandler {
    private static final Logger logger = Logger.getLogger(TexasCheckRequestHandler.class.getName());

    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.TexasCheckRequest request = messageRequest.getTexasCheckRequest();
        Jinli.TexasCheckReply.Builder reply = Jinli.TexasCheckReply.newBuilder();
        Texas texas = (Texas) DataManager.findGame(request.getGameId());
        RacePokerPlayer player = texas.getPlayerFromList(user.getId());
        if (texas.getGameState().get() != BETTING) {
            return buildReply(reply, GAME_STATUS_NOT_BETTING);
        }
        if (!texas.isNextTurn(player.getSeatNum())) {
            return buildReply(reply, NOT_GAME_MEMBER);
        }
        texas.check(player);
        return buildReply(reply, SUCCESS);
    }
}
