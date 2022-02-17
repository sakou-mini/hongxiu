package com.donglaistd.jinli.database.entity.game;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Game;
import com.donglaistd.jinli.builder.BullBullBuilder;
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
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.donglaistd.jinli.Constant.ResultCode.*;

public class BullBullTest extends BaseTest {
    @Autowired
    BullBullBuilder bullBullBuilder;

    @Autowired
    UserDaoService userDaoService;

    @Autowired
    DataManager dataManager;

    @Override
    public void setUp() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        super.setUp();
        EventPublisher.isEnabled = new AtomicBoolean(true);
    }

    public User creteUser(String name, int coin, int id) {
        User user1 = userBuilder.createUser(name + id, name + id, "admin" + id);
        user1.setGameCoin(coin);
        return user1;
    }

    public Deck createDeck() {
        var deck = new Deck();
        for (int i = 1; i <= 5; i++) {
            deck.addCard(new Card(Constant.CardNumber.forNumber(i), Constant.CardType.Spade));
        }
        for (int i = 1; i <= 5; i++) {
            deck.addCard(new Card(Constant.CardNumber.forNumber(i), Constant.CardType.Club));
        }
        for (int i = 1; i <= 5; i++) {
            deck.addCard(new Card(Constant.CardNumber.forNumber(i), Constant.CardType.Heart));
        }
        for (int i = 6; i <= 10; i++) {
            deck.addCard(new Card(Constant.CardNumber.forNumber(i), Constant.CardType.Spade));
        }
        for (int i = 1; i <= 5; i++) {
            deck.addCard(new Card(Constant.CardNumber.forNumber(i), Constant.CardType.Heart));
        }
        return deck;
    }

    public Deck createDeck2() {
        var deck = new Deck();
        for (int i = 1; i <= 5; i++) {
            deck.addCard(new Card(Constant.CardNumber.forNumber(i), Constant.CardType.Heart)); //2
        }
        for (int i = 1; i <= 5; i++) {
            deck.addCard(new Card(Constant.CardNumber.forNumber(i), Constant.CardType.Spade));  //1
        }
        for (int i = 1; i <= 5; i++) {
            deck.addCard(new Card(Constant.CardNumber.forNumber(i), Constant.CardType.Diamond)); //4
        }
        for (int i = 6; i <= 10; i++) {
            deck.addCard(new Card(Constant.CardNumber.forNumber(i), Constant.CardType.Spade)); //5
        }
        for (int i = 1; i <= 5; i++) {
            deck.addCard(new Card(Constant.CardNumber.forNumber(i), Constant.CardType.Club));  //3 banker
        }
        return deck;
    }

    @Test
    public void BullBullDealCardTest() {
        BullBull bullBull = bullBullBuilder.create(true);
        bullBull.deck = createDeck();
        bullBull.setOwner(liveUser);
        bullBull.dealCards();
        Assert.assertEquals(bullBull.getBankerCards().size(), 5);
        Assert.assertEquals(bullBull.getCardsByType(Game.BetType.SPADE_AREA).size(), 5);
        Assert.assertEquals(bullBull.getCardsByType(Game.BetType.HEART_AREA).size(), 5);
        Assert.assertEquals(bullBull.getCardsByType(Game.BetType.CLUB_AREA).size(), 5);
        Assert.assertEquals(bullBull.getCardsByType(Game.BetType.DIAMOND_AREA).size(), 5);
    }


    @Test
    public void BullBullBetAndSettleGameForSinglePlayerAndNoBankerTest() throws InterruptedException {
        BullBull bullBull = bullBullBuilder.create(true);
        bullBull.setOwner(liveUser);
        bullBull.deck = createDeck();

        Assert.assertEquals(Constant.GameStatus.PAUSED, bullBull.getGameStatus());
        User user1 = creteUser("zsf", 200, 20);
        dataManager.saveUser(user1);
        bullBull.beginGameLoop(1000);
        bullBull.setDelayFinishTime(1000);
        Pair<Constant.ResultCode, Long> betResult = bullBull.bet(user1, 200, Game.BetType.SPADE_AREA);
        Assert.assertEquals(NOT_ENOUGH_GAMECOIN,betResult.getLeft());
        betResult = bullBull.bet(user1, 20, Game.BetType.SPADE_AREA); //+4
        user1.setGameCoin(user1.getGameCoin() - betResult.getRight());
        Assert.assertEquals(SUCCESS,betResult.getLeft());
        dataManager.saveUser(user1);
        Assert.assertEquals(Constant.GameStatus.BETTING, bullBull.getGameStatus());
        Thread.sleep(1500);
        Assert.assertEquals(Constant.GameStatus.SETTLING, bullBull.getGameStatus());
        Assert.assertTrue(bullBull.getGameResult().contains(Game.BetType.SPADE_AREA));
        user1 = userDaoService.findById(user1.getId());
        Assert.assertEquals(257, user1.getGameCoin());    //200 - 60  + 60 - 20 +60 *0.95 + 20
        forceStop(bullBull);
    }

    @Test
    public void BullBullThreeBetAndBankerTest() throws InterruptedException {
        BullBull bullBull = bullBullBuilder.create(true);
        bullBull.setOwner(liveUser);
        bullBull.deck = createDeck2();
        bullBull.setDelayFinishTime(1000);

        Assert.assertEquals(Constant.GameStatus.PAUSED, bullBull.getGameStatus());
        User user1 = creteUser("zsf1", 5000, 20);
        User user2 = creteUser("xm1", 600, 10);
        User user3 = creteUser("xf1", 1000, 30);
        dataManager.saveUser(user1);
        dataManager.saveUser(user2);
        dataManager.saveUser(user3);
        User banker = creteUser("banker1", 10000, 50);
        banker.setOnline(true);
        dataManager.saveUser(banker);
        bullBull.addWaitingBanker(banker, 10000);
        bullBull.beginGameLoop(1000);

        Pair<Constant.ResultCode, Long> betResult = bullBull.bet(user1, 200, Game.BetType.SPADE_AREA);//+3payout
        user1.setGameCoin(user1.getGameCoin() - betResult.getRight());
        betResult = bullBull.bet(user1, 300, Game.BetType.HEART_AREA);  //+3payout
        user1.setGameCoin(user1.getGameCoin() - betResult.getRight());
        betResult = bullBull.bet(user1, 300, Game.BetType.CLUB_AREA);   //-3payout
        user1.setGameCoin(user1.getGameCoin() - betResult.getRight());
        betResult = bullBull.bet(user2, 200, Game.BetType.HEART_AREA);  //+3payout
        user2.setGameCoin(user2.getGameCoin() - betResult.getRight());
        betResult = bullBull.bet(user3, 200, Game.BetType.DIAMOND_AREA);//-3payout
        user3.setGameCoin(user3.getGameCoin() - betResult.getRight());
        dataManager.saveUser(user1);
        dataManager.saveUser(user2);
        dataManager.saveUser(user3);
        Assert.assertEquals(Constant.GameStatus.BETTING, bullBull.getGameStatus());

        Thread.sleep(2200);

        Assert.assertTrue(Arrays.asList(Game.BetType.HEART_AREA, Game.BetType.SPADE_AREA).containsAll(bullBull.getGameResult()));

        //(5000-2400)+ +2400-800+ (770+1155-600) = 5525
        user1 = userDaoService.findById(user1.getId());
        Assert.assertEquals(5525, user1.getGameCoin());

        //(600-600) + 600-200 + 770 = 1170
        user2 = userDaoService.findById(user2.getId());
        Assert.assertEquals(1170, user2.getGameCoin());

        //(1000-600)+600 - 200 -400 = 400
        user3 = userDaoService.findById(user3.getId());
        Assert.assertEquals(400, user3.getGameCoin());
        /*banker：
         * 10000-600-900+855-600+570
         * */
        Assert.assertEquals(9325, bullBull.bankerCoinAmount);
        forceStop(bullBull);
    }

    @Test
    public void BullBullBetLimitTest() throws InterruptedException {
        BullBull bullBull = bullBullBuilder.create(true);
        bullBull.setOwner(liveUser);
        bullBull.deck = createDeck2();
        bullBull.setDelayFinishTime(1000);
        User user1 = creteUser("zsf1", 5000, 20);
        User user2 = creteUser("xm1", 8000, 10);
        User user3 = creteUser("xf1", 100000, 30);
        User banker = creteUser("banker1", 50000, 50);
        dataManager.saveUser(user1);
        dataManager.saveUser(user2);
        dataManager.saveUser(user3);
        dataManager.saveUser(banker);
        banker.setQuitingBanker(false);
        banker.setOnline(true);

        bullBull.addWaitingBanker(banker, 10001);
        bullBull.beginGameLoop(1000);

        Pair<Constant.ResultCode, Long> betResult = bullBull.bet(user1, 10, Game.BetType.HEART_AREA);
        user1.setGameCoin(user1.getGameCoin() - betResult.getRight());
        Assert.assertEquals(SUCCESS,betResult.getLeft());
        betResult = bullBull.bet(user1, 10, Game.BetType.CLUB_AREA);
        user1.setGameCoin(user1.getGameCoin() - betResult.getRight());
        Assert.assertEquals(SUCCESS,betResult.getLeft());
        betResult = bullBull.bet(user1, 10, Game.BetType.DIAMOND_AREA);
        user1.setGameCoin(user1.getGameCoin() - betResult.getRight());
        Assert.assertEquals(SUCCESS,betResult.getLeft());
        betResult = bullBull.bet(user1, 20, Game.BetType.SPADE_AREA);
        user1.setGameCoin(user1.getGameCoin() - betResult.getRight());
        Assert.assertEquals(SUCCESS,betResult.getLeft());
        betResult = bullBull.bet(user2, 200, Game.BetType.HEART_AREA);
        user2.setGameCoin(user2.getGameCoin() - betResult.getRight());
        Assert.assertEquals(SUCCESS,betResult.getLeft());
        betResult = bullBull.bet(user2, 200, Game.BetType.CLUB_AREA);
        user2.setGameCoin(user2.getGameCoin() - betResult.getRight());
        Assert.assertEquals(SUCCESS,betResult.getLeft());
        betResult = bullBull.bet(user2, 200, Game.BetType.DIAMOND_AREA);
        user2.setGameCoin(user2.getGameCoin() - betResult.getRight());
        Assert.assertEquals(SUCCESS,betResult.getLeft());
        betResult = bullBull.bet(user2, 80, Game.BetType.SPADE_AREA);
        user2.setGameCoin(user2.getGameCoin() - betResult.getRight());
        Assert.assertEquals(SUCCESS,betResult.getLeft());
        betResult = bullBull.bet(user3, 200, Game.BetType.HEART_AREA);
        user3.setGameCoin(user3.getGameCoin() - betResult.getRight());
        Assert.assertEquals(SUCCESS,betResult.getLeft());
        betResult = bullBull.bet(user3, 200, Game.BetType.CLUB_AREA);
        user3.setGameCoin(user3.getGameCoin() - betResult.getRight());
        Assert.assertEquals(SUCCESS,betResult.getLeft());
        betResult = bullBull.bet(user3, 200, Game.BetType.DIAMOND_AREA);
        user3.setGameCoin(user3.getGameCoin() - betResult.getRight());
        Assert.assertEquals(SUCCESS,betResult.getLeft());
        betResult = bullBull.bet(user3, 2000, Game.BetType.SPADE_AREA);
        user3.setGameCoin(user3.getGameCoin() - betResult.getRight());
        Assert.assertEquals(SUCCESS,betResult.getLeft());
        betResult = bullBull.bet(user3, 20, Game.BetType.SPADE_AREA);
        Assert.assertEquals(EXCEED_BET_LIMIT,betResult.getLeft());
        dataManager.saveUser(user1);
        dataManager.saveUser(user2);
        dataManager.saveUser(user3);
        Thread.sleep(100);
        user1 = userDaoService.findById(user1.getId());
        user2 = userDaoService.findById(user2.getId());
        user3 = userDaoService.findById(user3.getId());
        Assert.assertEquals(4850,user1.getGameCoin()); //5000- 50 *3
        Assert.assertEquals(5960,user2.getGameCoin());//8000 - 680*3
        Assert.assertEquals(92200,user3.getGameCoin());// 100000 - 2600*3
        forceStop(bullBull);
    }
}
