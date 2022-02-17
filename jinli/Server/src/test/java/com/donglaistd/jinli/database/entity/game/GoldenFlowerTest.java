package com.donglaistd.jinli.database.entity.game;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Game;
import com.donglaistd.jinli.builder.GoldenFlowerBuilder;
import com.donglaistd.jinli.database.dao.RoomDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.Card;
import com.donglaistd.jinli.database.entity.Deck;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.donglaistd.jinli.Constant.ResultCode.*;

public class GoldenFlowerTest extends BaseTest {
    @Autowired
    GoldenFlowerBuilder goldenFlowerBuilder;
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    private RoomDaoService roomDaoService;
    @Autowired
    DataManager dataManager;

    protected User createTester(int coinCount, String name) {
        var user = userBuilder.createNoSavedUser(name, name, "","",coinCount,"",true);
        return user;
    }

    @Override
    public void setUp() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        super.setUp();
        EventPublisher.isEnabled = new AtomicBoolean(true);
    }

    public Deck getCardsDeck1() {
        Card card1 = new Card(Constant.CardNumber.Three, Constant.CardType.Club);
        Card card2 = new Card(Constant.CardNumber.Four, Constant.CardType.Diamond);
        Card card3 = new Card(Constant.CardNumber.Five, Constant.CardType.Diamond);

        Card card4 = new Card(Constant.CardNumber.Ace, Constant.CardType.Club);
        Card card5 = new Card(Constant.CardNumber.Two, Constant.CardType.Club);
        Card card6 = new Card(Constant.CardNumber.Five, Constant.CardType.Club);

        Card card7 = new Card(Constant.CardNumber.Ace, Constant.CardType.Club);
        Card card8 = new Card(Constant.CardNumber.Six, Constant.CardType.Diamond);
        Card card9 = new Card(Constant.CardNumber.King, Constant.CardType.Club);

        Card card10 = new Card(Constant.CardNumber.Jack, Constant.CardType.Club);
        Card card11 = new Card(Constant.CardNumber.Jack, Constant.CardType.Diamond);
        Card card12 = new Card(Constant.CardNumber.Ace, Constant.CardType.Heart);

        Card card13 = new Card(Constant.CardNumber.Six, Constant.CardType.Club);
        Card card14 = new Card(Constant.CardNumber.Seven, Constant.CardType.Club);
        Card card15 = new Card(Constant.CardNumber.Eight, Constant.CardType.Club);
        ArrayList<Card> cards = new ArrayList<>(List.of(card1, card2, card3, card4, card5, card6, card7, card8, card9,
                card10, card11, card12, card13, card14, card15));
        Deck deck = new Deck();
        cards.forEach(deck::addCard);
        return deck;
    }

    @Test
    public void noBankerAndTwoPlayerStartGameTest() throws InterruptedException {
        GoldenFlower goldenFlower = goldenFlowerBuilder.create(true);

        goldenFlower.setOwner(liveUser);
        goldenFlower.deck = getCardsDeck1();

        User user1 = createTester(3000, "张三丰");
        User user2 = createTester(800, "李四");
        dataManager.saveUser(user1);
        dataManager.saveUser(user2);

        goldenFlower.beginGameLoop(1000);
        goldenFlower.setDelayFinishTime(1000);
        //banker Straight  3
        Pair<Constant.ResultCode, Long> betResult = goldenFlower.bet(user1, 200, Game.BetType.SPADE_AREA);//GoldenFlower  5
        user1.setGameCoin(user1.getGameCoin() - betResult.getRight());
        betResult = goldenFlower.bet(user1, 200, Game.BetType.SPADE_AREA);
        user1.setGameCoin(user1.getGameCoin() - betResult.getRight());
        betResult = goldenFlower.bet(user2, 100, Game.BetType.HEART_AREA);
        user2.setGameCoin(user2.getGameCoin() - betResult.getRight());
        dataManager.saveUser(user1);
        dataManager.saveUser(user2);
        Thread.sleep(1800);
        user1 = userDaoService.findById(user1.getId());
        user2 = userDaoService.findById(user2.getId());
        Assert.assertEquals(Constant.GameStatus.SETTLING, goldenFlower.getGameStatus());
        Assert.assertTrue(goldenFlower.getGameResult().containsAll(List.of(Game.BetType.SPADE_AREA, Game.BetType.DIAMOND_AREA)));
        Assert.assertEquals(4900, user1.getGameCoin());//3000 - 2800 + 2800 - 400 +  (2000*0.95)+400
        Assert.assertEquals(400, user2.getGameCoin()); // 800 - 700 + 700 -100 -300
        forceStop(goldenFlower);
    }

    @Test
    public void noBankerAndTwoPlayerStartGameTest2() throws InterruptedException {
        GoldenFlower goldenFlower = goldenFlowerBuilder.create(true);

        goldenFlower.setOwner(liveUser);
        goldenFlower.deck = getCardsDeck1();

        User user1 = createTester(3000, "张三丰");
        User user2 = createTester(2800, "李四");
        dataManager.saveUser(user);
        dataManager.saveUser(user1);
        dataManager.saveUser(user2);

        goldenFlower.beginGameLoop(1000);
        goldenFlower.setDelayFinishTime(1000);
        //banker Straight  4
        Pair<Constant.ResultCode, Long> betResult = goldenFlower.bet(user1, 1000, Game.BetType.SPADE_AREA);//GoldenFlower  5
        Assert.assertEquals(NOT_ENOUGH_GAMECOIN,betResult.getLeft());

        betResult = goldenFlower.bet(user1, 200, Game.BetType.CLUB_AREA);//lost 4
        user1.setGameCoin(user1.getGameCoin()-betResult.getRight());

        betResult = goldenFlower.bet(user2, 400, Game.BetType.HEART_AREA);//lost 4
        user2.setGameCoin(user2.getGameCoin()-betResult.getRight());

        dataManager.saveUser(user1);
        dataManager.saveUser(user2);
        Thread.sleep(1500);
        user1 = userDaoService.findById(user1.getId());
        user2 = userDaoService.findById(user2.getId());
        Assert.assertEquals(Constant.GameStatus.SETTLING, goldenFlower.getGameStatus());
        Assert.assertTrue(goldenFlower.getGameResult().containsAll(List.of(Game.BetType.SPADE_AREA, Game.BetType.DIAMOND_AREA)));
        Assert.assertEquals(2200, user1.getGameCoin());  //3000 - 800
        Assert.assertEquals(1200, user2.getGameCoin());     // 2800 - 1600
        forceStop(goldenFlower);
    }

    public Deck getCardsDeck2() {
        Card card1 = new Card(Constant.CardNumber.Ten, Constant.CardType.Club);
        Card card2 = new Card(Constant.CardNumber.Ten, Constant.CardType.Diamond);
        Card card3 = new Card(Constant.CardNumber.Five, Constant.CardType.Diamond);

        Card card4 = new Card(Constant.CardNumber.Ace, Constant.CardType.Club);
        Card card5 = new Card(Constant.CardNumber.Two, Constant.CardType.Club);
        Card card6 = new Card(Constant.CardNumber.Five, Constant.CardType.Club);

        Card card7 = new Card(Constant.CardNumber.Six, Constant.CardType.Club);
        Card card8 = new Card(Constant.CardNumber.Six, Constant.CardType.Diamond);
        Card card9 = new Card(Constant.CardNumber.King, Constant.CardType.Club);

        Card card10 = new Card(Constant.CardNumber.Two, Constant.CardType.Diamond);
        Card card11 = new Card(Constant.CardNumber.Jack, Constant.CardType.Diamond);
        Card card12 = new Card(Constant.CardNumber.Ace, Constant.CardType.Heart);

        Card card13 = new Card(Constant.CardNumber.Six, Constant.CardType.Club);
        Card card14 = new Card(Constant.CardNumber.Seven, Constant.CardType.Club);
        Card card15 = new Card(Constant.CardNumber.Eight, Constant.CardType.Club);
        ArrayList<Card> cards = new ArrayList<>(List.of(card1, card2, card3, card4, card5, card6, card7, card8, card9,
                card10, card11, card12, card13, card14, card15));
        Deck deck = new Deck();
        cards.forEach(deck::addCard);
        return deck;
    }

    @Test
    public void bankerAndThreePlayerStartGameTest() throws InterruptedException {
        GoldenFlower goldenFlower = goldenFlowerBuilder.create(true);
        goldenFlower.setOwner(liveUser);
        goldenFlower.deck = getCardsDeck2();

        User user1 = createTester(7000, "zhang san");
        User user2 = createTester(8000, "li si");
        User user3 = createTester(1500, "citoho");
        dataManager.saveUser(user1);
        dataManager.saveUser(user2);
        dataManager.saveUser(user3);
        User banker = createTester(10000, "banker1");
        dataManager.saveUser(banker);
        banker.setOnline(true);
        goldenFlower.addWaitingBanker(banker, 10000);
        goldenFlower.beginGameLoop(1000);
        goldenFlower.setDelayFinishTime(1000);

        Pair<Constant.ResultCode, Long> betResult = goldenFlower.bet(user1, 1000, Game.BetType.SPADE_AREA);//win  5
        Assert.assertEquals(SUCCESS,betResult.getLeft());
        user1.setGameCoin(user1.getGameCoin() - betResult.getRight());

        betResult = goldenFlower.bet(user2, 1100, Game.BetType.CLUB_AREA);//lose  3 ,but cant bet,over banker limit
        Assert.assertEquals(EXCEED_BET_LIMIT,betResult.getLeft());

        betResult = goldenFlower.bet(user3, 200, Game.BetType.HEART_AREA);//lose  3
        user3.setGameCoin(user3.getGameCoin() - betResult.getRight());

        dataManager.saveUser(user1);
        dataManager.saveUser(user2);
        dataManager.saveUser(user3);
        Thread.sleep(1500);
        user1 = userDaoService.findById(user1.getId());
        user2 = userDaoService.findById(user2.getId());
        user3 = userDaoService.findById(user3.getId());
        Assert.assertEquals(Constant.GameStatus.SETTLING, goldenFlower.getGameStatus());
        Assert.assertTrue(goldenFlower.getGameResult().containsAll(List.of(Game.BetType.SPADE_AREA, Game.BetType.DIAMOND_AREA)));
        Assert.assertEquals(11750, user1.getGameCoin());//7000 - 1000 +  5750
        Assert.assertEquals(8000, user2.getGameCoin());
        Assert.assertEquals(900, user3.getGameCoin()); //dec max coin 600   1500-200 - 400
        // banker=  10000-4000+570
        Assert.assertEquals(6570, goldenFlower.bankerCoinAmount);
        forceStop(goldenFlower);
    }

    @Test

    public void goldenFlowerBetLimitTest() {
        GoldenFlower goldenFlower = goldenFlowerBuilder.create(true);
        goldenFlower.setOwner(liveUser);
        goldenFlower.deck = getCardsDeck2();

        User user1 = createTester(9000, "zsf");
        User user2 = createTester(6000, "lb");
        User user3 = createTester(8000, "xh");
        dataManager.saveUser(user1);
        dataManager.saveUser(user2);
        dataManager.saveUser(user3);
        User banker = createTester(10000, "banker1");
        dataManager.saveUser(banker);
        banker.setOnline(true);
        goldenFlower.addWaitingBanker(banker, 10000);
        goldenFlower.beginGameLoop(800);
        goldenFlower.setDelayFinishTime(200);
        //10000
        Pair<Constant.ResultCode, Long> betResult = goldenFlower.bet(user1, 300, Game.BetType.CLUB_AREA);
        Assert.assertEquals(SUCCESS, betResult.getLeft());
        user1.setGameCoin(user1.getGameCoin() - betResult.getRight());

        betResult = goldenFlower.bet(user1, 500, Game.BetType.DIAMOND_AREA);
        Assert.assertEquals(SUCCESS, betResult.getLeft());
        user1.setGameCoin(user1.getGameCoin() - betResult.getRight());

        betResult =  goldenFlower.bet(user2, 500, Game.BetType.SPADE_AREA);
        Assert.assertEquals(SUCCESS, betResult.getLeft());
        user2.setGameCoin(user2.getGameCoin() - betResult.getRight());

        betResult = goldenFlower.bet(user3, 500, Game.BetType.HEART_AREA);
        Assert.assertEquals(EXCEED_BET_LIMIT, betResult.getLeft());
    }
}
