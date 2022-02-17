package com.donglaistd.jinli.processors.handler.game;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Game;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.config.ConditionType;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.game.BaseGame;
import com.donglaistd.jinli.event.ModifyCoinEvent;
import com.donglaistd.jinli.event.TaskEvent;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.Pair;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.logging.Logger;

import static com.donglaistd.jinli.Constant.GameStatus.BETTING;
import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class BetRequestHandler extends GameHandler {
    Logger logger = Logger.getLogger(GameHandler.class.getName());
    @Autowired
    DataManager dataManager;
    @Override
    public Pair<Game.GameReply, Constant.ResultCode> handle(ChannelHandlerContext ctx, Game.GameRequest messageRequest, BaseGame game) {
        var reply = Game.GameReply.newBuilder();
        var betReply = Game.BetReply.newBuilder();
        User user = dataManager.getUserFromChannel(ctx);
        var request = messageRequest.getBetRequest();
        logger.info("bet game" +game.getGameType()+"-------------------------->");
        int betAmount = request.getBetAmount();
        var betType = request.getBetType();
        if (user.getGameCoin() < betAmount) {
            return generateReply(reply, betReply, NOT_ENOUGH_GAMECOIN);
        }

        if (game.getGameStatus().equals(BETTING)) {
            return generateReply(reply, betReply, processBet(user, betType, betAmount, game));
        } else {
            return generateReply(reply, betReply, IS_NOT_BET_STATUE);
        }
    }

    private Pair<Game.GameReply, Constant.ResultCode> generateReply(Game.GameReply.Builder reply, Game.BetReply.Builder betReply, Constant.ResultCode resultCode) {
        reply.setBetReply(betReply);
        return new Pair<>(reply.build(), resultCode);
    }


    private Constant.ResultCode processBet(User user, Game.BetType betType, int betAmount, BaseGame game) {
        var betResult = game.bet(user, betAmount, betType);
        if (betResult.getLeft().equals(SUCCESS)) {
            EventPublisher.publish(new ModifyCoinEvent(user, -betResult.getRight()));
            EventPublisher.publish(TaskEvent.newInstance(user.getId(), ConditionType.bet,betAmount));
            var bbm = Jinli.BetBroadcastMessage.newBuilder();
            bbm.setBetAmount(betAmount);
            bbm.setBetType(betType);
            bbm.setUserId(user.getId());
            bbm.setRoomId(game.getOwner().getRoomId());
            user = dataManager.findUser(user.getId());
            bbm.setUserCoin(user.getGameCoin());
            bbm.setTotalBetNum(game.getBetTypeAmountMap().getOrDefault(betType,0L));
            Room room = DataManager.roomMap.getOrDefault(game.getOwner().getRoomId(), DataManager.closeRoomInfo.get(game.getOwner().getRoomId()));
            if (Objects.nonNull(room)) room.broadCastToAllPlatform(buildReply(bbm, SUCCESS));
            return SUCCESS;
        } else {
            return betResult.getLeft();
        }
    }
}
