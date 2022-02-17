package com.donglaistd.jinli.processors.handler.game;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Game;
import com.donglaistd.jinli.builder.LonghuBuilder;
import com.donglaistd.jinli.database.dao.RoomDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.game.Longhu;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.util.DataManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.Game.BetType.LONG;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BetRequestHandlerTest extends BaseTest {
    @Autowired
    LonghuBuilder longhuBuilder;
    @Autowired
    BetRequestHandler handler;
    @Autowired
    RoomDaoService roomDaoService;

    @Override
    public void setUp() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        super.setUp();
        EventPublisher.isEnabled = new AtomicBoolean(true);
    }

    @Override
    public void tearDown() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        super.tearDown();
        EventPublisher.isEnabled = new AtomicBoolean(false);
    }

    @Test
    public void TestBetNotEnoughCoin() {
        var gameBuilder = Game.GameRequest.newBuilder();
        var betRequest = Game.BetRequest.newBuilder();
        betRequest.setBetAmount(100);
        betRequest.setBetType(LONG);
        gameBuilder.setBetRequest(betRequest);
        Longhu longhu = longhuBuilder.create();
        var reply = handler.handle(context, gameBuilder.build(), longhu);
        Assert.assertEquals(Constant.GameStatus.PAUSED, longhu.getGameStatus());
        Assert.assertEquals(NOT_ENOUGH_GAMECOIN, reply.getRight());
    }

    @Test
    public void TestBetErrorByStatus() {
        user.setGameCoin(100);
        dataManager.saveUser(user);
        var gameBuilder = Game.GameRequest.newBuilder();
        var betRequest = Game.BetRequest.newBuilder();
        betRequest.setBetAmount(100);
        betRequest.setBetType(LONG);
        gameBuilder.setBetRequest(betRequest);
        Longhu longhu = longhuBuilder.create();
        var reply = handler.handle(context, gameBuilder.build(), longhu);
        Assert.assertEquals(Constant.GameStatus.PAUSED, longhu.getGameStatus());
        Assert.assertEquals(IS_NOT_BET_STATUE, reply.getRight());
    }

    @Test
    public void TestBetSuccessful() {
        user.setGameCoin(100);
        dataManager.saveUser(user);
        var gameBuilder = Game.GameRequest.newBuilder();
        var betRequest = Game.BetRequest.newBuilder();
        betRequest.setBetAmount(100);
        betRequest.setBetType(LONG);
        gameBuilder.setBetRequest(betRequest);
        Longhu longhu = longhuBuilder.create();
        longhu.setGameStatus(Constant.GameStatus.BETTING);
        longhu.setOwner(liveUser);
        liveUser.setPlayingGameId(longhu.getGameId());
        dataManager.saveLiveUser(liveUser);
        DataManager.addGame(longhu);
        var reply = handler.handle(context, gameBuilder.build(), longhu);
        user = userDaoService.findById(user.getId());
        Assert.assertEquals(SUCCESS, reply.getRight());
        Assert.assertEquals(0, user.getGameCoin());
    }

    @Test
    public void TestBetConcurrently() throws InterruptedException {
        user.setGameCoin(400);
        dataManager.saveUser(user);
        var gameBuilder = Game.GameRequest.newBuilder();
        var betRequest = Game.BetRequest.newBuilder();
        betRequest.setBetAmount(100);
        betRequest.setBetType(LONG);
        gameBuilder.setBetRequest(betRequest);
        Longhu longhu = longhuBuilder.create();
        longhu.setGameStatus(Constant.GameStatus.BETTING);
        longhu.setOwner(liveUser);
        liveUser.setPlayingGameId(longhu.getGameId());
        dataManager.saveLiveUser(liveUser);
        DataManager.addGame(longhu);
        var t1 = new Thread(() -> handler.handle(context, gameBuilder.build(), longhu));
        var t2 = new Thread(() -> handler.handle(context, gameBuilder.build(), longhu));
        t1.start();
        t2.start();
        Thread.sleep(1000);
        user = userDaoService.findById(user.getId());
        Assert.assertEquals(200, user.getGameCoin());
    }
}
