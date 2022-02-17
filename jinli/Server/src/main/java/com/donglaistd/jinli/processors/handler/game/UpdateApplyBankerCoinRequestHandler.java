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

import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.util.DataManager.getRoomFromChannel;
@Component
public class UpdateApplyBankerCoinRequestHandler extends GameHandler {
    @Autowired
    DataManager dataManager;

    @Override
    public Pair<Game.GameReply, Constant.ResultCode> handle(ChannelHandlerContext ctx, Game.GameRequest messageRequest, BaseGame game) {
        Game.UpdateApplyBankerCoinRequest request = messageRequest.getUpdateApplyBankerCoinRequest();
        var reply = Game.GameReply.newBuilder();
        Game.UpdateApplyBankerCoinReply.Builder replyBuilder = Game.UpdateApplyBankerCoinReply.newBuilder();
        var user = dataManager.getUserFromChannel(ctx);
        var room = getRoomFromChannel(ctx);
        if (room == null || room.notContainsUser(user)) {
            return generateReply(reply, replyBuilder, NO_ENTERED_ROOM);
        }
        if (game instanceof BankerGame && ((BankerGame) game).isOpenBanker()) {
            var bankerGame = (BankerGame) game;
            Constant.ResultCode resultCode = bankerGame.updateWaitingBankerBringCoin(user, request.getBringCoin());
            return generateReply(reply, replyBuilder,resultCode);
        } else {
            return generateReply(reply, replyBuilder, NOT_BANKER_GAME);
        }
    }

    private Pair<Game.GameReply, Constant.ResultCode> generateReply(Game.GameReply.Builder reply, Game.UpdateApplyBankerCoinReply.Builder replyBuilder, Constant.ResultCode resultCode) {
        reply.setUpdateApplyBankerCoinReply(replyBuilder);
        return new Pair<>(reply.build(), resultCode);
    }
}
