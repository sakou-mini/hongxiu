package com.donglaistd.jinli.processors.handler.race;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.builder.RaceBuilder;
import com.donglaistd.jinli.config.GameInit;
import com.donglaistd.jinli.config.LandlordRaceConfig;
import com.donglaistd.jinli.database.entity.race.LandlordsRace;
import com.donglaistd.jinli.database.entity.race.UserRace;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.landlords.LandLordsDataUtil;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class QueryJoinedRaceGameInfoHandlerTest extends BaseTest {

    @Autowired
    JoinRaceRequestHandler joinLandlordRaceRequestHandler;
    @Autowired
    QueryJoinedRaceGameInfoRequestHandler queryJoinedRaceGameInfoRequestHandler;
    @Autowired
    GameInit gameInit;
    @Test
    public void queryJoinRaceGameTest() throws InterruptedException {
        user.setGameCoin(2000);
        List<LandlordsRace> races = RaceBuilder.filterOpenLandlordRace(DataManager.getAllRaceByRaceType(Constant.RaceType.LANDLORDS));
        if(races.isEmpty()) {
            gameInit.initLandLordsRace();
            races = RaceBuilder.filterOpenLandlordRace(DataManager.getAllRaceByRaceType(Constant.RaceType.LANDLORDS));
        }
        LandlordsRace race = races.get(0);
        LandLordsDataUtil.mockJoinRaceWithUserNum(race,race.getRaceConfig().getJoinPeopleNum() - race.getJoinQueues().size() -1);
        Jinli.JoinRaceRequest.Builder joinRace = Jinli.JoinRaceRequest.newBuilder().setRaceId(race.getId()).setRaceType(Constant.RaceType.LANDLORDS);
        Jinli.JinliMessageRequest.Builder builder = Jinli.JinliMessageRequest.newBuilder().setJoinRaceRequest(joinRace);
        Jinli.JinliMessageReply result = joinLandlordRaceRequestHandler.doHandle(context, builder.build(), user);
        Assert.assertEquals(Constant.ResultCode.SUCCESS,result.getResultCode());
        Thread.sleep(2200);

        UserRace userRace = DataManager.findUserRace(user.getId());
        Jinli.QueryJoinedRaceGameInfoRequest.Builder request = Jinli.QueryJoinedRaceGameInfoRequest.newBuilder().setGameId(userRace.getGameId());
        builder = Jinli.JinliMessageRequest.newBuilder().setQueryJoinedRaceGameInfoRequest(request);
        Jinli.JinliMessageReply reply = queryJoinedRaceGameInfoRequestHandler.doHandle(context, builder.build(), user);

        Jinli.QueryJoinedRaceGameInfoReply gameInfoReply = reply.getQueryJoinedRaceGameInfoReply();
        Assert.assertEquals(Constant.ResultCode.SUCCESS,reply.getResultCode());
        Assert.assertNotNull(gameInfoReply.getUserLandlordsGame().getGameInfo());
    }
}
