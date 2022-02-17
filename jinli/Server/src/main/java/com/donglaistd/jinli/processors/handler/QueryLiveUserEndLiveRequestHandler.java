package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.config.ConditionType;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.game.BaseGame;
import com.donglaistd.jinli.event.TaskEvent;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.service.RoomProcess;
import com.donglaistd.jinli.util.DataManager;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.util.MessageUtil.buildReply;


@Component
public class QueryLiveUserEndLiveRequestHandler extends MessageHandler {
    @Autowired
    DataManager dataManager;
    @Value("${room.income.rate}")
    private double roomInComeRate;
    @Autowired
    RoomProcess roomProcess;

    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.QueryLiveUserEndLiveReply.Builder reply = Jinli.QueryLiveUserEndLiveReply.newBuilder();
        LiveUser liveUser = dataManager.findLiveUser(user.getLiveUserId());
        if (Objects.isNull(liveUser)) {
            resultCode = Constant.ResultCode.NOT_LIVE_USER;
            return buildReply(reply, resultCode);
        }
        Room room = DataManager.closeRoomInfo.containsKey(liveUser.getRoomId()) ? DataManager.closeRoomInfo.remove(liveUser.getRoomId()) : DataManager.findOnlineRoom(liveUser.getRoomId());
        if (room == null) {
            resultCode = Constant.ResultCode.ROOM_DOES_NOT_EXIST;
            return buildReply(reply, resultCode);
        }
        long totalGiftCoin = room.getTotalGiftCoin();
        //long totalIncome = BigDecimal.valueOf(room.getTotalBetAmount()).multiply(BigDecimal.valueOf(roomInComeRate)).setScale(2, RoundingMode.CEILING).longValue();
        reply.setStartDate(room.getStartDate().getTime()).setTotalBetAmount(room.getTotalBetAmount()).setAvatarUrl(user.getAvatarUrl());
        reply.addAllLiveRanks(roomProcess.buildLiveRankByLiveRankPair(room.getTopGiftRank()));
        reply.setTotalIncome(totalGiftCoin).setGiftTotalCoin(totalGiftCoin);
        cleanGameInfo(room);
        resultCode = Constant.ResultCode.SUCCESS;
        updateLiveTask(room,user.getId());
        return buildReply(reply, resultCode);
    }

    private void updateLiveTask(Room room,String userId){
        long liveTime = System.currentTimeMillis() - room.getStartDate().getTime();
        liveTime = TimeUnit.MILLISECONDS.toMinutes(liveTime);
        EventPublisher.publish(TaskEvent.newInstance(userId, ConditionType.onLive,liveTime));
    }

    public void cleanGameInfo(Room room) {
        List<BaseGame> games = DataManager.findAllGame().stream().filter(g -> g instanceof BaseGame).map(g -> (BaseGame) g)
                .filter(baseGame -> baseGame.getOwner().getRoomId().equals(room.getId())).collect(Collectors.toList());
        games.forEach(game -> {
            if (game.isStop()) {
                DataManager.removeGame(game.getGameId());
            }
        });
    }
}
