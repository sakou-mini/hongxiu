package com.donglaistd.jinli.processors.handler.race.landlord;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.builder.RaceBuilder;
import com.donglaistd.jinli.config.GameInit;
import com.donglaistd.jinli.database.entity.game.landlord.Landlords;
import com.donglaistd.jinli.database.entity.race.LandlordsRace;
import com.donglaistd.jinli.database.entity.race.RaceBase;
import com.donglaistd.jinli.processors.handler.race.JoinRaceRequestHandler;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.landlords.LandLordsDataUtil;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import static com.donglaistd.jinli.Constant.RaceStep.Race_FirstRound;
import static com.donglaistd.jinli.Constant.RaceStep.Race_LastRound;

public class JoinLandlordRaceRequestHandlerTest extends BaseTest {
    Logger logger = Logger.getLogger(JoinLandlordRaceRequestHandlerTest.class.getName());
    @Autowired
    JoinRaceRequestHandler joinLandlordRaceRequestHandler;
    @Autowired
    GameInit gameInit;
    @Test
    public void joinRaceTest() throws InterruptedException {
        List<RaceBase> landlordRace = DataManager.getAllRaceByRaceType(Constant.RaceType.LANDLORDS);
        if(landlordRace.isEmpty())
            gameInit.initLandLordsRace();
        EventPublisher.isEnabled = new AtomicBoolean(true);
        List<LandlordsRace> races = RaceBuilder.filterOpenLandlordRace(landlordRace);
        LandlordsRace race = races.get(0);
        LandLordsDataUtil.mockJoinRaceWithUserNum(race,race.getRaceConfig().getJoinPeopleNum() - race.getJoinQueues().size() -1);
        race.getRaceConfig().setFirstRaceRound(1);
        race.getRaceConfig().setSecondRaceRound(1);
        Jinli.JoinRaceRequest.Builder joinRace = Jinli.JoinRaceRequest.newBuilder().setRaceId(race.getId()).setRaceType(Constant.RaceType.LANDLORDS);
        Jinli.JinliMessageRequest.Builder builder = Jinli.JinliMessageRequest.newBuilder().setJoinRaceRequest(joinRace);
        user.setGameCoin(10000);
        Jinli.JinliMessageReply reply = joinLandlordRaceRequestHandler.doHandle(context, builder.build(), user);
        Assert.assertEquals(Constant.ResultCode.SUCCESS,reply.getResultCode());
        Thread.sleep(2200);
        if(race.getStep().equals(Race_FirstRound)){
            List<String> gameIds = race.getGameIds();
            ArrayList<String> tempGames = Lists.newArrayList(gameIds);
            Landlords game;
            for (String gameId : tempGames) {
                game = (Landlords) DataManager.findGame(gameId);
                game.getConfig().setEndWaitTime(0);
                game.enterSettleStep();
            }
            Thread.sleep(2500);
            Assert.assertEquals(Race_LastRound,race.getStep());
            Assert.assertEquals(3, race.getPromotionPLayers().size());
        }
    }
}
