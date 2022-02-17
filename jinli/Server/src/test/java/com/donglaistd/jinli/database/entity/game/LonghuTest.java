package com.donglaistd.jinli.database.entity.game;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Game;
import com.donglaistd.jinli.builder.LonghuBuilder;
import com.donglaistd.jinli.database.dao.RoomDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.Card;
import com.donglaistd.jinli.database.entity.Deck;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.util.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;

public class LonghuTest extends BaseTest {
    @Autowired
    LonghuBuilder longhuBuilder;
    @Autowired
    private UserDaoService userDaoService;
    @Autowired
    private RoomDaoService roomDaoService;

    @Override
    public void setUp() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        super.setUp();
        EventPublisher.isEnabled = new AtomicBoolean(true);
    }

    @Test
    public void SinglePlayerBetResultTest() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        var deck = new Deck();
        deck.addCard(new Card(Constant.CardNumber.Two, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Ace, Constant.CardType.Spade));
        var longhu = longhuBuilder.create();
        var field = longhu.getClass().getSuperclass().getSuperclass().getDeclaredField("deck");
        field.setAccessible(true);
        field.set(longhu, deck);
        longhu.setGameStatus(Constant.GameStatus.PAUSED);
        Room room = new Room();
        dataManager.saveUser(user);
        liveUser.setRoomId(room.getId());
        dataManager.saveRoom(room);
        roomDaoService.save(room);
        liveUser.setRoomId(room.getId());
        longhu.setOwner(liveUser);
        var userA = createTester(100, "A");
        userDaoService.save(user);
        userDaoService.save(userA);
        longhu.setPayRate(new BigDecimal("0.95"));
        longhu.beginGameLoop(1000);
        Pair<Constant.ResultCode, Long> bet = longhu.bet(userA, 100, Game.BetType.LONG);
        userA.setGameCoin(userA.getGameCoin()-bet.getRight());
        dataManager.saveUser(userA);
        Thread.sleep(1500);
        userA = userDaoService.findById(userA.getId());
        Assert.assertEquals(Constant.GameStatus.SETTLING, longhu.getGameStatus());
        Assert.assertEquals(Game.BetType.LONG, longhu.getGameResult().get(0));
        Assert.assertEquals(95, userA.getGameCoin());
        forceStop(longhu);
    }

    @Test
    public void MultiplePlayerBetResultTest() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        var deck = new Deck();
        deck.addCard(new Card(Constant.CardNumber.Ten, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Nine, Constant.CardType.Spade));
        var longhu = longhuBuilder.create();
        var field = longhu.getClass().getSuperclass().getSuperclass().getDeclaredField("deck");
        field.setAccessible(true);
        field.set(longhu, deck);
        longhu.setGameStatus(Constant.GameStatus.PAUSED);
        Room room = new Room();
        dataManager.saveUser(user);
        liveUser.setRoomId(room.getId());
        dataManager.saveRoom(room);
        roomDaoService.save(room);
        liveUser.setRoomId(room.getId());
        longhu.setOwner(liveUser);

        var userA = createTester(100, "A");
        var userB = createTester(100, "B");
        userDaoService.save(user);
        userDaoService.save(userA);
        userDaoService.save(userB);
        longhu.setPayRate(new BigDecimal("0.95"));
        longhu.beginGameLoop(1000);
        longhu.setDelayFinishTime(1000);
        Pair<Constant.ResultCode, Long> betResult = longhu.bet(userA, 100, Game.BetType.LONG);
        userA.setGameCoin(userA.getGameCoin()-betResult.getRight());
        betResult = longhu.bet(userB, 100, Game.BetType.HU);
        userB.setGameCoin(userB.getGameCoin()-betResult.getRight());
        dataManager.saveUser(userA);
        dataManager.saveUser(userB);
        Thread.sleep(1500);
        userA = userDaoService.findById(userA.getId());
        userB = userDaoService.findById(userB.getId());
        Assert.assertEquals(Constant.GameStatus.SETTLING, longhu.getGameStatus());
        Assert.assertEquals(Game.BetType.LONG, longhu.getGameResult().get(0));
        Assert.assertEquals(190, userA.getGameCoin());
        Assert.assertEquals(0, userB.getGameCoin());
        forceStop(longhu);
    }

    @Test
    public void ThreePlayerBetResultTest() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        var deck = new Deck();
        deck.addCard(new Card(Constant.CardNumber.Ten, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Nine, Constant.CardType.Spade));
        var longhu = longhuBuilder.create();
        var field = longhu.getClass().getSuperclass().getSuperclass().getDeclaredField("deck");
        field.setAccessible(true);
        field.set(longhu, deck);
        longhu.setOwner(liveUser);
        Room room = new Room();
        dataManager.saveUser(user);
        liveUser.setRoomId(room.getId());
        dataManager.saveRoom(room);
        roomDaoService.save(room);
        liveUser.setRoomId(room.getId());
        longhu.setGameStatus(Constant.GameStatus.PAUSED);
        longhu.setPayRate(new BigDecimal("0.95"));
        var userA = createTester(100, "A");
        var userB = createTester(100, "B");
        var userC = createTester(100, "C");
        userDaoService.save(user);
        userDaoService.save(userA);
        userDaoService.save(userB);
        userDaoService.save(userC);
        longhu.beginGameLoop(1000);
        Pair<Constant.ResultCode, Long> betResult = longhu.bet(userA, 50, Game.BetType.LONG);
        userA.setGameCoin(userA.getGameCoin() - betResult.getRight());
        betResult = longhu.bet(userB, 80, Game.BetType.LONG);
        userB.setGameCoin(userB.getGameCoin() - betResult.getRight());
        betResult = longhu.bet(userC, 100, Game.BetType.HU);
        userC.setGameCoin(userC.getGameCoin() - betResult.getRight());
        dataManager.saveUser(userA);
        dataManager.saveUser(userB);
        dataManager.saveUser(userC);

        Thread.sleep(1500);
        userA = userDaoService.findById(userA.getId());
        userB = userDaoService.findById(userB.getId());
        userC = userDaoService.findById(userC.getId());
        Assert.assertEquals(Constant.GameStatus.SETTLING, longhu.getGameStatus());
        Assert.assertEquals(Game.BetType.LONG, longhu.getGameResult().get(0));
        Assert.assertEquals(134, userA.getGameCoin());
        Assert.assertEquals(154, userB.getGameCoin());
        Assert.assertEquals(0, userC.getGameCoin());
        forceStop(longhu);
    }

    @Test
    public void DeckShuffleTest() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        var deck = new Deck();
        deck.addCard(new Card(Constant.CardNumber.Ace, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Two, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Three, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Four, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Three, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Four, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Five, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Nine, Constant.CardType.Spade));
        var longhu = longhuBuilder.create();
        var field = longhu.getClass().getSuperclass().getSuperclass().getDeclaredField("deck");
        field.setAccessible(true);
        field.set(longhu, deck);
        var field2 = longhu.getClass().getDeclaredField("minimalDeckCardRequest");
        field2.setAccessible(true);
        field2.set(longhu, 5);
        longhu.setGameStatus(Constant.GameStatus.PAUSED);
        longhu.setPayRate(new BigDecimal("0.95"));
        Room room = new Room();
        dataManager.saveUser(user);
        liveUser.setRoomId(room.getId());
        dataManager.saveRoom(room);
        roomDaoService.save(room);
        liveUser.setRoomId(room.getId());
        longhu.setOwner(liveUser);
        var userA = createTester(100, "A");

        userDaoService.save(user);
        userDaoService.save(userA);
        longhu.beginGameLoop(1000);
        longhu.setDelayFinishTime(1000);
        longhu.bet(userA, 100, Game.BetType.LONG);
        Assert.assertEquals(6, longhu.getDeckLeftCount());
        Assert.assertEquals(2, longhu.getDeckDealtCount());
        Thread.sleep(2500);
        Assert.assertEquals(4, longhu.getDeckLeftCount());
        Assert.assertEquals(4, longhu.getDeckDealtCount());
        Thread.sleep(2000);
        Assert.assertEquals(6, longhu.getDeckLeftCount());
        Assert.assertEquals(2, longhu.getDeckDealtCount());
        forceStop(longhu);
    }
}
