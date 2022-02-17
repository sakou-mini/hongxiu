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

import static com.donglaistd.jinli.Constant.GameStatus.BETTING;
import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class TexasFoldRequestHandler extends MessageHandler {

    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.TexasFoldRequest request = messageRequest.getTexasFoldRequest();
        Jinli.TexasFoldReply.Builder reply = Jinli.TexasFoldReply.newBuilder();
        Texas texas = (Texas) DataManager.findGame(request.getGameId());
        RacePokerPlayer player = texas.getPlayerFromList(user.getId());
        if (Objects.isNull(player)) return buildReply(reply, NOT_GAME_MEMBER);
        // 状态游戏中
        if (!texas.getGameState().get().equals(BETTING)) {
            return buildReply(reply, GAME_STATUS_NOT_BETTING);
        }
        if (!texas.isNextTurn(player.getSeatNum())) {
            return buildReply(reply, ILLEGAL_OPERATION);
        }
        texas.fold(player);
        return buildReply(reply, SUCCESS);
    }
}
