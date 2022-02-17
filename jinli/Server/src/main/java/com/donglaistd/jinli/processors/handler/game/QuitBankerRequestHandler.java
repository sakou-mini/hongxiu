package com.donglaistd.jinli.processors.handler.game;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Game;
import com.donglaistd.jinli.database.entity.game.BankerGame;
import com.donglaistd.jinli.database.entity.game.BaseGame;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.Pair;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.donglaistd.jinli.Constant.ResultCode.NOT_BANKER_GAME;
import static com.donglaistd.jinli.Constant.ResultCode.NO_ENTERED_ROOM;
import static com.donglaistd.jinli.util.DataManager.getRoomFromChannel;


@Component
public class QuitBankerRequestHandler extends GameHandler {

    @Autowired
    DataManager dataManager;

    @Override
    public Pair<Game.GameReply, Constant.ResultCode> handle(ChannelHandlerContext ctx, Game.GameRequest messageRequest, BaseGame game) {
        var user = dataManager.getUserFromChannel(ctx);
        var reply = Game.GameReply.newBuilder();
        var quitBankerReply = Game.QuitBankerReply.newBuilder();
        var room = getRoomFromChannel(ctx);
        if (room == null || room.notContainsUser(user)) {
            return generateReply(reply, quitBankerReply, NO_ENTERED_ROOM);
        }
        if (game instanceof BankerGame) {
            var bankerGame = (BankerGame) game;
            return generateReply(reply, quitBankerReply, bankerGame.quitBanker(user,false));
        } else {
            return generateReply(reply, quitBankerReply, NOT_BANKER_GAME);
        }
    }

    private Pair<Game.GameReply, Constant.ResultCode> generateReply(Game.GameReply.Builder reply,
                                                                  Game.QuitBankerReply.Builder startGameReply, Constant.ResultCode resultCode) {
        reply.setQuitBankerReply(startGameReply);
        return new Pair<>(reply.build(), resultCode);
    }
}
