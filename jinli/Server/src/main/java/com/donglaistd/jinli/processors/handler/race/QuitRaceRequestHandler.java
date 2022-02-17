package com.donglaistd.jinli.processors.handler.race;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.race.GoldenFlowerRace;
import com.donglaistd.jinli.database.entity.race.TexasRace;
import com.donglaistd.jinli.database.entity.race.LandlordsRace;
import com.donglaistd.jinli.event.ModifyCoinEvent;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.util.DataManager;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class QuitRaceRequestHandler extends MessageHandler {

    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.QuitRaceRequest request = messageRequest.getQuitRaceRequest();
        Jinli.QuitRaceReply.Builder reply = Jinli.QuitRaceReply.newBuilder();
        Constant.ResultCode resultCode = quitRace(request, user);
        return buildReply(reply, resultCode);
    }

    public Constant.ResultCode quitRace(Jinli.QuitRaceRequest request, User user) {
        String raceId = request.getRaceId();
        var race = DataManager.findRace(raceId);
        if (race == null) return RACE_NOT_EXISTS;
        int fee = 0;
        switch (race.getRaceType()) {
            case LANDLORDS:
                if (!(race instanceof LandlordsRace)) return RACE_NOT_EXISTS;
                LandlordsRace landlordRace = (LandlordsRace) race;
                if (!landlordRace.containUser(user)) return SUCCESS;
                if (!landlordRace.quitRace(user)) return QUIT_RACE_FAILED;
                fee = landlordRace.getRaceConfig().getRaceFee();
                EventPublisher.publish(new ModifyCoinEvent(user, fee));
                return SUCCESS;
            case TEXAS:
                TexasRace texasRace = (TexasRace) DataManager.findRace(raceId);
                fee = texasRace.getConfig().getRaceFee() + texasRace.getConfig().getServiceCharge();
                if (!texasRace.quitRace(user)) return QUIT_RACE_FAILED;
                EventPublisher.publish(new ModifyCoinEvent(user, fee));
                return SUCCESS;
            case GOLDEN_FLOWER:
                GoldenFlowerRace goldenFlowerRace = (GoldenFlowerRace) DataManager.findRace(raceId);
                 fee = goldenFlowerRace.getConfig().getRaceFee();
                if (!goldenFlowerRace.quitRace(user)) return QUIT_RACE_FAILED;
                EventPublisher.publish(new ModifyCoinEvent(user, fee));
                return SUCCESS;
        }
        return Constant.ResultCode.RACE_NOT_EXISTS;
    }
}
