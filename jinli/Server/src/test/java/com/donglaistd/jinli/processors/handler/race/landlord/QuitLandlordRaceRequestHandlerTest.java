package com.donglaistd.jinli.processors.handler.race.landlord;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.builder.RaceBuilder;
import com.donglaistd.jinli.config.GameInit;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.race.LandlordsRace;
import com.donglaistd.jinli.database.entity.race.RaceBase;
import com.donglaistd.jinli.processors.handler.race.JoinRaceRequestHandler;
import com.donglaistd.jinli.processors.handler.race.QuitRaceRequestHandler;
import com.donglaistd.jinli.util.DataManager;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.donglaistd.jinli.Constant.RaceStep.Race_Open;
import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;

public class QuitLandlordRaceRequestHandlerTest extends BaseTest {
    @Autowired
    QuitRaceRequestHandler quitLandlordRaceRequestHandler;

    @Autowired
    JoinRaceRequestHandler joinRaceRequestHandler;
    @Autowired
    GameInit gameInit;

    @Test
    public void quitLandlordRaceTest(){
        List<RaceBase> landlordRace = DataManager.getAllRaceByRaceType(Constant.RaceType.LANDLORDS);
        if(landlordRace.isEmpty()) {
            gameInit.initLandLordsRace();
        }
        user.setGameCoin(10000);
        LandlordsRace race  = RaceBuilder.filterOpenLandlordRace(landlordRace).get(0);
        if(!race.getJoinQueues().isEmpty())
            race.getJoinQueues().remove(0);
        Jinli.JoinRaceRequest.Builder joinRace = Jinli.JoinRaceRequest.newBuilder().setRaceId(race.getId()).setRaceType(Constant.RaceType.LANDLORDS);
        Jinli.JinliMessageRequest.Builder builder = Jinli.JinliMessageRequest.newBuilder().setJoinRaceRequest(joinRace);
        Jinli.JinliMessageReply reply = joinRaceRequestHandler.doHandle(context, builder.build(), user);
        Assert.assertEquals(SUCCESS,reply.getResultCode());
        boolean contain = race.getJoinQueues().stream().anyMatch(player -> player.getUser().equals(user));
        Assert.assertTrue(contain);
        //USER QUIE Race
        Jinli.QuitRaceRequest.Builder quitRaceRequest = Jinli.QuitRaceRequest.newBuilder().setRaceId(race.getId()).setRaceType(Constant.RaceType.LANDLORDS);
        builder = Jinli.JinliMessageRequest.newBuilder().setQuitRaceRequest(quitRaceRequest);
        Jinli.JinliMessageReply quitRaceReply = quitLandlordRaceRequestHandler.doHandle(context, builder.build(), user);
        Assert.assertEquals(Race_Open,race.getStep());
        Assert.assertEquals(SUCCESS,quitRaceReply.getResultCode());

        //OtherJoin
        User tester = createTester(200, "无名");
        tester.setGameCoin(10000);
        joinRace = Jinli.JoinRaceRequest.newBuilder().setRaceId(race.getId()).setRaceType(Constant.RaceType.LANDLORDS);
        builder = Jinli.JinliMessageRequest.newBuilder().setJoinRaceRequest(joinRace);
        reply = joinRaceRequestHandler.doHandle(context, builder.build(), tester);
        Assert.assertEquals(SUCCESS,reply.getResultCode());
        //Other Quite
        quitRaceRequest = Jinli.QuitRaceRequest.newBuilder().setRaceId(race.getId()).setRaceType(Constant.RaceType.LANDLORDS);
        builder = Jinli.JinliMessageRequest.newBuilder().setQuitRaceRequest(quitRaceRequest);
        quitRaceReply = quitLandlordRaceRequestHandler.doHandle(context, builder.build(), user);
        Assert.assertEquals(SUCCESS,quitRaceReply.getResultCode());
    }


}
