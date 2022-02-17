package com.donglaistd.jinli.processors.handler.game;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Game;
import com.donglaistd.jinli.database.entity.game.BankerGame;
import com.donglaistd.jinli.database.entity.game.BaseGame;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.Pair;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.util.DataManager.getRoomFromChannel;


@Component
public class BecomeBankerRequestHandler extends GameHandler {

    @Autowired
    DataManager dataManager;
    @Value("${banker.limit.num}")
    private long maxBanker;

    @Override
    public Pair<Game.GameReply, Constant.ResultCode> handle(ChannelHandlerContext ctx, Game.GameRequest messageRequest, BaseGame game) {
        synchronized (game){
            var user = dataManager.getUserFromChannel(ctx);
            var reply = Game.GameReply.newBuilder();
            var becomeBankerReply = Game.BecomeBankerReply.newBuilder();
            var room = getRoomFromChannel(ctx);
            if (room == null || room.notContainsUser(user)) {
                return generateReply(reply, becomeBankerReply, NO_ENTERED_ROOM);
            }
            if (game instanceof BankerGame && ((BankerGame) game).isOpenBanker()) {
                var bankerGame = (BankerGame) game;
                if(bankerGame.getBankerSize() >= maxBanker) return generateReply(reply, becomeBankerReply, BANKER_OVER_LIMIT);
                return generateReply(reply, becomeBankerReply, bankerGame.addWaitingBanker(user, messageRequest.getBecomeBankerRequest().getBankerCoin()));
            } else {
                return generateReply(reply, becomeBankerReply, NOT_BANKER_GAME);
            }
        }
    }

    private Pair<Game.GameReply, Constant.ResultCode> generateReply(Game.GameReply.Builder reply, Game.BecomeBankerReply.Builder startGameReply, Constant.ResultCode resultCode) {
        reply.setBecomeBankerReply(startGameReply);
        return new Pair<>(reply.build(), resultCode);
    }
}
