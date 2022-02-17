package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Game;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.game.BankerGame;
import com.donglaistd.jinli.database.entity.game.BaseGame;
import com.donglaistd.jinli.database.entity.game.BullBull;
import com.donglaistd.jinli.database.entity.game.EmptyGame;
import com.donglaistd.jinli.util.DataManager;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class GameInfoRequestHandler extends MessageHandler {
    @Autowired
    DataManager dataManager;

    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        var request = messageRequest.getGameInfoRequest();
        var room = DataManager.roomMap.get(request.getRoomId());
        var reply = Jinli.GameInfoReply.newBuilder();
        if (room == null || dataManager.findLiveUser(room.getLiveUserId()) == null) {
            resultCode = Constant.ResultCode.ROOM_DOES_NOT_EXIST;
            return buildReply(reply, resultCode);
        }
        BaseGame game = null;
        if (dataManager.findLiveUser(room.getLiveUserId()).getPlayingGameId() != null) {
            game = (BaseGame) DataManager.findGame(dataManager.findLiveUser(room.getLiveUserId()).getPlayingGameId());
        }
        if (game != null && !(game instanceof EmptyGame)) {
            var betAmountMap = game.getBetAmountMap();
            for (var perUserBetAmount : betAmountMap.entrySet()) {
                for (var betAmount : perUserBetAmount.getValue().entrySet()) {
                    var betInfo = Jinli.BetInfo.newBuilder();
                    betInfo.setBetAmount(betAmount.getValue());
                    betInfo.setBetType(betAmount.getKey());
                    betInfo.setUserId(perUserBetAmount.getKey().getDisplayName());
                    reply.addBetInfo(betInfo);
                }
            }
            reply.setGameStatus(game.getGameStatus());
            reply.setTimeToEnd(game.getTimeCountDown());
            reply.setLeftCardCount(game.getDeckLeftCount());
            reply.setDealtCardCount(game.getDeckDealtCount());
            reply.setGameId(game.getGameId());
            reply.setGameType(game.getGameType());
            reply.addAllGameResult(game.getCardHistory());
            reply.setDealtCardsBroadcastMessage(game.getDeckDealt());
            reply.setNextGameStatus(game.getNextGameStatue());
            if (game.getClass().getSuperclass().equals(BankerGame.class)) {
                var bankerGame = (BankerGame) game;
                reply.setBankerMinimalCoin(bankerGame.getBankerMinimalCoin());
                reply.setBankerContinueCoin(bankerGame.getBankerContinueCoin());
                reply.setBankerKeepCount(bankerGame.getBankerKeepCount());
                reply.setBankerWinAmount(bankerGame.getBankerWinAmount());
                reply.setBankerBroadcastMessage(bankerGame.getBankerInfoMessage());
                reply.setIsBankerGame(bankerGame.isOpenBanker());
            }
            if (game instanceof BullBull) {
                var gameDetail = Game.GameDetail.newBuilder();
                gameDetail.setBullBullCardResult(Game.BullBullGameDetail.newBuilder().setBullbullCardShow(((BullBull) game).getCardResult()));
                reply.setGameDetail(gameDetail);
            }
        } else if (game != null) {
            reply.setGameType(game.getGameType());
            reply.setGameId(game.getGameId());
        }
        resultCode = Constant.ResultCode.SUCCESS;
        reply.setRoomId(room.getId());
        return buildReply(reply, resultCode);
    }
}
