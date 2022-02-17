package com.donglaistd.jinli.processors.handler.race;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.race.*;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.Pair;
import com.google.common.base.Strings;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class QueryJoinedRaceRequestHandler extends MessageHandler {
    @Value("${texas.baseReward}")
    private int BASEREWARD;
    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.QueryJoinedRaceRequest request = messageRequest.getQueryJoinedRaceRequest();
        String raceId = request.getRaceId();
        Pair<Jinli.QueryJoinedRaceReply.Builder, Constant.ResultCode> pair = process(raceId, user);
        return  buildReply(pair.getLeft(), pair.getRight());
    }

    public Pair<Jinli.QueryJoinedRaceReply.Builder, Constant.ResultCode> process(String raceId, User user) {
        Jinli.QueryJoinedRaceReply.Builder reply = Jinli.QueryJoinedRaceReply.newBuilder();
        RaceBase race = DataManager.findRace(raceId);
        UserRace userRace = DataManager.findUserRace(user.getId());
        if(race == null) return new Pair<>(reply, RACE_NOT_EXISTS);
        if(userRace == null || !userRace.getRaceId().equals(raceId))
            return new Pair<>(reply, NOT_JOIN_RACE);
        switch (race.getRaceType()){
            case LANDLORDS:
                LandlordsRace landlordsRace = (LandlordsRace) race;
                reply.setLandlordRace(landlordsRace.toProto());
                if(!Strings.isNullOrEmpty(userRace.getGameId()))
                    reply.setRaceGameId(userRace.getGameId());
                return new Pair<>(reply, SUCCESS);
            case TEXAS:
                TexasRace textRace = (TexasRace) race;
                Jinli.TexasRaceReply.Builder builder = Jinli.TexasRaceReply.newBuilder();
                int rewardAmount = textRace.getSize() * BASEREWARD;
                Jinli.TexasRaceConfig.Builder config = textRace.getConfig().toProto(rewardAmount,textRace.getStartTime()).toBuilder().setRewardAmount(((TexasRace) race).getSize() * BASEREWARD);
                builder.setChips(textRace.getConfig()
                        .getServiceCharge())
                        .setRaceType(textRace.getRaceType())
                        .setUser(user.toSummaryProto())
                        .setRaceId(textRace.getId())
                        .setConfig(config);
                reply.setTexasRace(builder.build());
                if(!Strings.isNullOrEmpty(userRace.getGameId()))
                    reply.setRaceGameId(userRace.getGameId());
                return new Pair<>(reply, SUCCESS);
            case GOLDEN_FLOWER:
                GoldenFlowerRace flowerRace = (GoldenFlowerRace) race;
                int reward = flowerRace.getSize() * flowerRace.getConfig().getBaseReward();
                Jinli.GoldenFlowerRaceConfig raceConfig = flowerRace.getConfig().toProto(reward,flowerRace.getStartTime());
                Jinli.GoldenFlowerReply.Builder RaceBuilder = Jinli.GoldenFlowerReply.newBuilder();
                RaceBuilder.setConfig(raceConfig).setUser(user.toSummaryProto()).setRaceType(flowerRace.getRaceType()).setRaceId(flowerRace.getId()).setChips(flowerRace.getConfig().getStartingChips());
                reply.setGoldenFlowerRace(RaceBuilder.build());
                if(!Strings.isNullOrEmpty(userRace.getGameId()))
                    reply.setRaceGameId(userRace.getGameId());
                return new Pair<>(reply, SUCCESS);
        }
        return null;
    }

}
