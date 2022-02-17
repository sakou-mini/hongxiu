package com.donglaistd.jinli.processors.handler.race;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.race.RaceBase;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.Pair;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class JoinRaceByRaceFeeRequestHandler extends MessageHandler {
    @Autowired
    JoinRaceRequestHandler joinRaceRequestHandler;

    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.JoinRaceByRaceFeeRequest request = messageRequest.getJoinRaceByRaceFeeRequest();
        Jinli.JoinRaceByRaceFeeReply.Builder replyBuilder = Jinli.JoinRaceByRaceFeeReply.newBuilder();
        Constant.RaceType raceType = request.getRaceType();
        int raceFee = request.getRaceFee();
        Optional<RaceBase> race = DataManager.getAllRaceByRaceType(raceType).stream().filter(raceBase -> raceBase.getRaceFee() == raceFee).findFirst();
        if(race.isEmpty()){
            return buildReply(replyBuilder);
        }
        RaceBase raceBase = race.get();
        Pair<Jinli.JoinRaceReply.Builder, Constant.ResultCode> processResult = joinRaceRequestHandler.process(raceType, raceBase.getId(), user);
        Jinli.JoinRaceReply.Builder joinReply = processResult.getLeft();
        replyBuilder.setGoldenFlowerRace(joinReply.getGoldenFlowerRace());
        replyBuilder.setLandlordRace(joinReply.getLandlordRace());
        replyBuilder.setTexasRace(joinReply.getTexasRace());
        return buildReply(replyBuilder,processResult.getRight());
    }


}
