package com.donglaistd.jinli.database.entity.game.landlord;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.builder.LandlordsBuilder;
import com.donglaistd.jinli.builder.LandlordsRaceBuilder;
import com.donglaistd.jinli.builder.RaceBuilder;
import com.donglaistd.jinli.config.LandlordGameConfig;
import com.donglaistd.jinli.database.entity.race.LandlordsRace;
import com.donglaistd.jinli.database.entity.race.UserRace;
import com.donglaistd.jinli.processors.handler.race.JoinRaceRequestHandler;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.StringUtils;
import com.donglaistd.jinli.util.landlords.LandLordsDataUtil;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static com.donglaistd.jinli.Constant.LandlordsStep.AddPayRate_step;
import static com.donglaistd.jinli.Constant.LandlordsStep.PlayCard_step;
import static com.donglaistd.jinli.Constant.RaceStep.Race_FirstRound;

public class LandlordsTest extends BaseTest {
    @Autowired
    LandlordsBuilder landlordsBuilder;
    @Autowired
    LandlordsRaceBuilder landlordsRaceBuilder;

    @Test
    public void createPokerGameTest() throws InterruptedException {
        List<PokerPlayer> pokerPlayers = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            PokerPlayer pokerPlayer = new PokerPlayer(createTester(10, "张三" + i), i + 1, 1000);
            pokerPlayers.add(pokerPlayer);
        }
        Landlords landlords = landlordsBuilder.create(pokerPlayers,"11");
        LandlordGameConfig config = landlords.getConfig();
        int baseRate = config.getBaseRate();
        config.setMessageDelayTime(0);
        landlords.setNextTurnIndex(1);
        landlords.startGame();
        Thread.sleep(1000);
        boolean result = landlords.grabLandlord(pokerPlayers.get(1), true);
        Assert.assertTrue(result);
        Thread.sleep(1000);

        result = landlords.grabLandlord(pokerPlayers.get(2), true);
        Assert.assertTrue(result);
        Thread.sleep(1000);

        result = landlords.grabLandlord(pokerPlayers.get(0), false);
        Assert.assertTrue(result);
        Thread.sleep(100);
        Assert.assertEquals(AddPayRate_step,landlords.getStep());
        result = landlords.plusRate(pokerPlayers.get(2), true);
        Assert.assertTrue(result);

        Assert.assertEquals(120,baseRate *  landlords.getUserRate(pokerPlayers.get(2).getUserId()));
        result = landlords.plusRate(pokerPlayers.get(0), false);
        Assert.assertTrue(result);
        result = landlords.plusRate(pokerPlayers.get(1), true);
        Assert.assertEquals(120,baseRate * landlords.getUserRate(pokerPlayers.get(1).getUserId()));
        Assert.assertEquals(180,baseRate * landlords.getUserRate(pokerPlayers.get(2).getUserId()));
        Assert.assertTrue(result);
        Thread.sleep(100);
        Assert.assertEquals(PlayCard_step,landlords.getStep());
        landlords.shutDownGame();
        landlords.cleanTimeTask();
    }

    @Autowired
    JoinRaceRequestHandler joinRaceRequestHandler;

    @Test
    public void joinLandlordsRaceTest() throws InterruptedException {
        user.setGameCoin(10000);
        List<LandlordsRace> races = RaceBuilder.filterOpenLandlordRace(DataManager.getAllRaceByRaceType(Constant.RaceType.LANDLORDS));
        LandlordsRace landlordsRace = null;
        if(races.isEmpty()) {
            landlordsRace = landlordsRaceBuilder.create();
            DataManager.addRace(landlordsRace);
        }else
            landlordsRace = races.get(0);
        LandLordsDataUtil.mockJoinRaceWithUserNum(landlordsRace,landlordsRace.getRaceConfig().getJoinPeopleNum() - landlordsRace.getJoinQueues().size() -1);

        Jinli.JoinRaceRequest.Builder joinRace = Jinli.JoinRaceRequest.newBuilder().setRaceId(landlordsRace.getId()).setRaceType(Constant.RaceType.LANDLORDS);
        Jinli.JinliMessageRequest.Builder builder = Jinli.JinliMessageRequest.newBuilder().setJoinRaceRequest(joinRace);
        Jinli.JinliMessageReply reply = joinRaceRequestHandler.doHandle(context, builder.build(), user);
        Assert.assertEquals(Constant.ResultCode.SUCCESS, reply.getResultCode());
        Thread.sleep(2200);
        UserRace userRace = DataManager.findUserRace(user.getId());
        if(!StringUtils.isNullOrBlank(userRace.getGameId())){
            String gameId = userRace.getGameId();
            Assert.assertEquals(Race_FirstRound,landlordsRace.getStep());

            Landlords oldGame = (Landlords) DataManager.findGame(gameId);
            oldGame.cleanTimeTask();
            Assert.assertEquals(landlordsRace.getId(),oldGame.getRaceId());

            Thread.sleep(3000);
            races = RaceBuilder.filterOpenLandlordRace(DataManager.getAllRaceByRaceType(Constant.RaceType.LANDLORDS));
            landlordsRace = races.get(0);
            joinRace.setRaceId(landlordsRace.getId()).setRaceType(Constant.RaceType.LANDLORDS);
            builder.setJoinRaceRequest(joinRace);
            reply =  joinRaceRequestHandler.doHandle(context, builder.build(), user);
            Assert.assertEquals(Constant.ResultCode.ALREADY_JOIN_RACE, reply.getResultCode());
        }
    }
}
