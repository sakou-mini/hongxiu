package com.donglaistd.jinli.processors.handler.race;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.annotation.IgnoreShutDown;
import com.donglaistd.jinli.builder.RaceBuilder;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.race.GoldenFlowerRace;
import com.donglaistd.jinli.database.entity.race.LandlordsRace;
import com.donglaistd.jinli.database.entity.race.TexasRace;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.service.GoldenFlowerProcess;
import com.donglaistd.jinli.service.LandlordRaceProcess;
import com.donglaistd.jinli.service.TexasRaceProcess;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.Pair;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@IgnoreShutDown
@Component
public class JoinRaceRequestHandler extends MessageHandler {
    @Autowired
    private LandlordRaceProcess landlordRaceProcess;
    @Autowired
    private TexasRaceProcess texasRaceProcess;
    @Autowired
    private GoldenFlowerProcess goldenFlowerProcess;
    @Value("${texas.baseReward}")
    private int BASEREWARD;
    private static final Object lockObject = new Object();
    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        synchronized (lockObject){
            Jinli.JoinRaceRequest request = messageRequest.getJoinRaceRequest();
            String raceId = request.getRaceId();
            Constant.RaceType raceType = request.getRaceType();
            Pair<Jinli.JoinRaceReply.Builder, Constant.ResultCode> pair = process(raceType, raceId, user);
            assert pair != null;
            return buildReply(pair.getLeft(), pair.getRight());
        }
    }

    public Pair<Jinli.JoinRaceReply.Builder, Constant.ResultCode> process(Constant.RaceType type, String raceId, User user) {
        Jinli.JoinRaceReply.Builder reply = Jinli.JoinRaceReply.newBuilder();
        switch (type) {
            case TEXAS:
                TexasRace texasRace = (TexasRace) DataManager.findRace(raceId);
                Constant.ResultCode code = texasRaceProcess.joinRace(user, texasRace);
                if (code.equals(SUCCESS)) {
                    int rewardAmount = texasRace.getSize() * BASEREWARD;
                    Jinli.TexasRaceConfig config = texasRace.getConfig().toProto(rewardAmount,texasRace.getStartTime());
                    reply.setTexasRace(Jinli.TexasRaceReply.newBuilder().setConfig(config).setChips(texasRace.getConfig().getStartingChips()).setUser(user.toSummaryProto()).build());
                }
                return new Pair<>(reply, code);
            case LANDLORDS:
                LandlordsRace landlordRace = (LandlordsRace) DataManager.findRace(raceId);
                landlordRace = landlordRace == null ? RaceBuilder.getNotOpenLandlordRace() : landlordRace;
                Constant.ResultCode resultCode = landlordRaceProcess.joinRace(user, landlordRace);
                if (resultCode.equals(SUCCESS)) {
                    reply.setLandlordRace(landlordRace.toProto());
                }else if(resultCode.equals(JOIN_RACE_FAILED) || resultCode.equals(RACE_NOT_EXISTS)) {
                    landlordRace = RaceBuilder.getNotOpenLandlordRace();
                    resultCode = landlordRaceProcess.joinRace(user, landlordRace);
                }
                return new Pair<>(reply, resultCode);
            case GOLDEN_FLOWER:
                GoldenFlowerRace goldenFlowerRace = (GoldenFlowerRace) DataManager.findRace(raceId);
                Constant.ResultCode reCode = goldenFlowerProcess.joinRace(user, goldenFlowerRace);
                    if (reCode.equals(SUCCESS)) {
                    int rewardAmount = goldenFlowerRace.getSize() * goldenFlowerRace.getConfig().getBaseReward();
                    Jinli.GoldenFlowerRaceConfig config = goldenFlowerRace.getConfig().toProto(rewardAmount,goldenFlowerRace.getStartTime());
                    Jinli.GoldenFlowerReply.Builder builder = Jinli.GoldenFlowerReply.newBuilder();
                    builder.setConfig(config)
                            .setUser(user.toSummaryProto())
                            .setRaceType(goldenFlowerRace.getRaceType())
                            .setRaceId(goldenFlowerRace.getId())
                            .setChips(goldenFlowerRace.getConfig().getStartingChips());
                    reply.setGoldenFlowerRace(builder.build());
                }
                return new Pair<>(reply, reCode);
        }
        return null;
    }
}
