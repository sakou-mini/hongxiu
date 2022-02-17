package com.donglaistd.jinli.database.entity.game;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Game;
import com.donglaistd.jinli.builder.RedBlackBuilder;
import com.donglaistd.jinli.database.dao.RoomDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.*;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.util.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class RedBlackTest extends BaseTest {
    private static final Logger logger = Logger.getLogger(RedBlackTest.class.getName());
    @Autowired
    RedBlackBuilder redBlackBuilder;

    @Autowired
    UserDaoService userDaoService;
    @Autowired
    private RoomDaoService roomDaoService;

    public User creteUser(String name, int coin, int id) {
        User user = userBuilder.createUser(name + id, name + id, "admin" + id);
        user.setGameCoin(coin);
        return user;
    }

    @Override
    public void setUp() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        super.setUp();
        EventPublisher.isEnabled = new AtomicBoolean(true);
    }

    @Test

    public void RedBlackRunStaticTest() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        var redBlack = redBlackBuilder.create(true);
        var field2 = redBlack.getClass().getDeclaredField("minimalDeckCardRequest");
        field2.setAccessible(true);
        field2.set(redBlack, 5);
        redBlack.setGameStatus(Constant.GameStatus.PAUSED);
        redBlack.setPayRate(new BigDecimal("0.95"));

        userDaoService.save(user);
        dataManager.saveUser(user);
        redBlack.setOwner(liveUser);
        redBlack.beginGameLoop(300);

        Thread.sleep(200);
        Assert.assertEquals(Constant.GameStatus.BETTING, redBlack.getGameStatus());
        Thread.sleep(200);
        Assert.assertEquals(Constant.GameStatus.SETTLING, redBlack.getGameStatus());
        forceStop(redBlack);
    }


    @Test

    public void RedBlackResultTest() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        var deck = new Deck();
        deck.addCard(new Card(Constant.CardNumber.Two, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Three, Constant.CardType.Club));
        deck.addCard(new Card(Constant.CardNumber.Four, Constant.CardType.Spade));

        deck.addCard(new Card(Constant.CardNumber.Four, Constant.CardType.Heart));
        deck.addCard(new Card(Constant.CardNumber.Nine, Constant.CardType.Club));
        deck.addCard(new Card(Constant.CardNumber.Five, Constant.CardType.Spade));

        deck.addCard(new Card(Constant.CardNumber.Five, Constant.CardType.Diamond));
        deck.addCard(new Card(Constant.CardNumber.Nine, Constant.CardType.Spade));

        var redBlack = redBlackBuilder.create(true);

        var field = redBlack.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredField("deck");
        field.setAccessible(true);
        field.set(redBlack, deck);
        redBlack.deck = deck;
        logger.fine(redBlack.deck.cards.toString());

        var field2 = redBlack.getClass().getDeclaredField("minimalDeckCardRequest");
        field2.setAccessible(true);
        field2.set(redBlack, 5);
        redBlack.setGameStatus(Constant.GameStatus.PAUSED);
        redBlack.setPayRate(new BigDecimal("0.95"));
        dataManager.saveUser(user);
        liveUser.setRoomId(room.getId());
        liveUser.setRoomId(room.getId());
        liveUser.setLiveStatus(Constant.LiveStatus.ONLINE);
        liveUser.setLiveUrl("");
        redBlack.setOwner(liveUser);

        redBlack.beginGameLoop(1000);
        var userA = creteUser("qqq", 1000, 20);
        userDaoService.save(userA);
        Pair<Constant.ResultCode, Long> betResult = redBlack.bet(userA, 100, Game.BetType.RED);
        Assert.assertEquals(Constant.ResultCode.SUCCESS, betResult.getLeft());
        userA.setGameCoin(userA.getGameCoin() - betResult.getRight());

        betResult = redBlack.bet(userA, 100, Game.BetType.BLACK);
        Assert.assertEquals(Constant.ResultCode.BETTYPE_MUTUAL_EXCLUSION, betResult.getLeft());

        betResult = redBlack.bet(userA, 100, Game.BetType.RED_BLACK_LUCk);
        Assert.assertEquals(Constant.ResultCode.SUCCESS, betResult.getLeft());
        userA.setGameCoin(userA.getGameCoin() - betResult.getRight());
        dataManager.saveUser(userA);
        Thread.sleep(1500);
        userA = userDaoService.findById(userA.getId());
        Assert.assertEquals(1285, userA.getGameCoin());
        forceStop(redBlack);
    }

    @Test
    public void RedBlackCardTest() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        var redBlack = redBlackBuilder.create(true);
        var field2 = redBlack.getClass().getDeclaredField("minimalDeckCardRequest");
        field2.setAccessible(true);
        field2.set(redBlack, 5);
        redBlack.setGameStatus(Constant.GameStatus.PAUSED);
        redBlack.setPayRate(new BigDecimal("0.95"));

        userDaoService.save(user);
        liveUser.setUserId(user.getId());
        dataManager.saveUser(user);
        liveUser.setLiveStatus(Constant.LiveStatus.ONLINE);
        liveUser.setLiveUrl("");
        redBlack.setOwner(liveUser);

        redBlack.beginGameLoop(1000);
        redBlack.setDelayFinishTime(0);
        Assert.assertEquals(6, redBlack.getDeckDealtCount());
        Assert.assertEquals(46, redBlack.getDeckLeftCount());
        Thread.sleep(1500);
        Assert.assertEquals(12, redBlack.getDeckDealtCount());
        Assert.assertEquals(40, redBlack.getDeckLeftCount());
        Thread.sleep(1000);
        Assert.assertEquals(18, redBlack.getDeckDealtCount());
        Assert.assertEquals(34, redBlack.getDeckLeftCount());
        forceStop(redBlack);
    }


    @Test
    public void RedBlackBetLostTest() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        var redBlack = redBlackBuilder.create(true);
        Deck deck = new Deck();
        deck.addCard(new Card(Constant.CardNumber.Two, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Five, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Four, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Three, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Six, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Seven, Constant.CardType.Spade));
        redBlack.deck = deck;
        redBlack.setGameStatus(Constant.GameStatus.PAUSED);
        redBlack.setPayRate(new BigDecimal("0.95"));
        Room room = new Room();
        room.setDescription("123");
        room.setRoomTitle("111");
        liveUser.setRoomId(room.getId());
        liveUser.setLiveStatus(Constant.LiveStatus.ONLINE);
        liveUser.setLiveUrl("");
        redBlack.setOwner(liveUser);
        dataManager.saveRoom(room);

        var userB = creteUser("xxx", 100000, 22);
        userB.setOnline(true);
        dataManager.saveUser(user);
        dataManager.saveUser(userB);
        redBlack.addWaitingBanker(userB, 10000);
        redBlack.beginGameLoop(1000);
        var userA = creteUser("qqq", 100000, 20);
        dataManager.saveUser(userA);
        Pair<Constant.ResultCode, Long> betResult = redBlack.bet(userA, 2000, Game.BetType.RED);
        userA.setGameCoin(userA.getGameCoin() - betResult.getRight());
        Assert.assertEquals(Constant.ResultCode.SUCCESS, betResult.getLeft());

        betResult = redBlack.bet(userA, 2000, Game.BetType.RED);
        userA.setGameCoin(userA.getGameCoin() - betResult.getRight());
        Assert.assertEquals(Constant.ResultCode.SUCCESS, betResult.getLeft());

        betResult = redBlack.bet(userA, 2000, Game.BetType.RED);
        userA.setGameCoin(userA.getGameCoin() - betResult.getRight());
        Assert.assertEquals(Constant.ResultCode.SUCCESS, betResult.getLeft());

        betResult = redBlack.bet(userA, 2000, Game.BetType.RED);
        userA.setGameCoin(userA.getGameCoin() - betResult.getRight());
        Assert.assertEquals(Constant.ResultCode.SUCCESS, betResult.getLeft());

        betResult = redBlack.bet(userA, 2000, Game.BetType.RED);
        userA.setGameCoin(userA.getGameCoin() - betResult.getRight());
        Assert.assertEquals(Constant.ResultCode.SUCCESS, betResult.getLeft());

        betResult = redBlack.bet(userA, 2000, Game.BetType.RED);
        userA.setGameCoin(userA.getGameCoin() - betResult.getRight());
        Assert.assertEquals(Constant.ResultCode.SUCCESS, betResult.getLeft());

        betResult = redBlack.bet(userA, 2000, Game.BetType.RED);
        Assert.assertEquals(Constant.ResultCode.EXCEED_BET_LIMIT, betResult.getLeft());
        dataManager.saveUser(userA);
        Thread.sleep(1500);
        userA = userDaoService.findById(userA.getId());
        Assert.assertEquals(88000, userA.getGameCoin());
        forceStop(redBlack);
    }

    @Test
    public void RedBlackBetWinTest() throws InterruptedException {
        var redBlack = redBlackBuilder.create(true);
        Deck deck = new Deck();
        deck.addCard(new Card(Constant.CardNumber.Two, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Two, Constant.CardType.Diamond));
        deck.addCard(new Card(Constant.CardNumber.Two, Constant.CardType.Club));
        deck.addCard(new Card(Constant.CardNumber.Three, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Six, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Seven, Constant.CardType.Spade));
        redBlack.deck = deck;
        redBlack.setGameStatus(Constant.GameStatus.PAUSED);
        redBlack.setPayRate(new BigDecimal("0.95"));
        Room room = new Room();
        room.setDescription("123");
        room.setRoomTitle("111");
        liveUser.setRoomId(room.getId());
        liveUser.setLiveStatus(Constant.LiveStatus.ONLINE);
        liveUser.setLiveUrl("");
        redBlack.setOwner(liveUser);
        dataManager.saveRoom(room);

        var userB = creteUser("xxx", 100000, 22);
        userB.setOnline(true);
        dataManager.saveUser(userB);
        redBlack.addWaitingBanker(userB, 10000);
        redBlack.beginGameLoop(1000);
        var userA = creteUser("qqq", 100000, 20);
        dataManager.saveUser(userA);
        Pair<Constant.ResultCode, Long> betResult = redBlack.bet(userA, 2000, Game.BetType.RED);
        Assert.assertEquals(Constant.ResultCode.SUCCESS, betResult.getLeft());
        userA.setGameCoin(userA.getGameCoin() - betResult.getRight());

        betResult = redBlack.bet(userA, 2000, Game.BetType.RED);
        Assert.assertEquals(Constant.ResultCode.SUCCESS, betResult.getLeft());
        userA.setGameCoin(userA.getGameCoin() - betResult.getRight());

        betResult = redBlack.bet(userA, 2000, Game.BetType.RED);
        Assert.assertEquals(Constant.ResultCode.SUCCESS, betResult.getLeft());
        userA.setGameCoin(userA.getGameCoin() - betResult.getRight());

        betResult = redBlack.bet(userA, 2000, Game.BetType.RED);
        Assert.assertEquals(Constant.ResultCode.SUCCESS, betResult.getLeft());
        userA.setGameCoin(userA.getGameCoin() - betResult.getRight());

        betResult = redBlack.bet(userA, 2000, Game.BetType.RED);
        Assert.assertEquals(Constant.ResultCode.SUCCESS, betResult.getLeft());
        userA.setGameCoin(userA.getGameCoin() - betResult.getRight());

        betResult = redBlack.bet(userA, 2000, Game.BetType.RED);
        Assert.assertEquals(Constant.ResultCode.SUCCESS, betResult.getLeft());
        userA.setGameCoin(userA.getGameCoin() - betResult.getRight());

        betResult = redBlack.bet(userA, 2000, Game.BetType.RED);
        Assert.assertEquals(Constant.ResultCode.EXCEED_BET_LIMIT, betResult.getLeft());
        dataManager.saveUser(userA);
        Thread.sleep(1500);
        userA = userDaoService.findById(userA.getId());
        Assert.assertEquals(111400, userA.getGameCoin());
        forceStop(redBlack);
    }
}
