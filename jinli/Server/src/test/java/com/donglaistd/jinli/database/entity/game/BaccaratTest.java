package com.donglaistd.jinli.database.entity.game;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Game;
import com.donglaistd.jinli.builder.BaccaratBuilder;
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
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("unchecked")
public class BaccaratTest extends BaseTest {

    @Autowired
    BaccaratBuilder baccaratBuilder;

    @Autowired
    UserDaoService userDaoService;

    @Override
    public void setUp() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        super.setUp();
        EventPublisher.isEnabled = new AtomicBoolean(true);
    }

    @Test
    public void testBaccaratDealCardBothLowerThanThree() throws NoSuchFieldException, IllegalAccessException {
        var deck = new Deck();
        deck.addCard(new Card(Constant.CardNumber.Ace, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Ace, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Ace, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Ace, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Ace, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Ace, Constant.CardType.Spade));
        var baccarat = new Baccarat(deck, 6);

        baccarat.dealCards();
        var f = baccarat.getClass().getDeclaredField("dealerCards");
        var f2 = baccarat.getClass().getDeclaredField("playerCards");
        f.setAccessible(true);
        f2.setAccessible(true);
        var dealerCards = (List<Card>) f.get(baccarat);
        var playerCards = (List<Card>) f2.get(baccarat);
        Assert.assertEquals(3, dealerCards.size());
        Assert.assertEquals(3, playerCards.size());
    }

    @Test
    public void testBaccaratDealCardPlayerLowerThanThree() throws NoSuchFieldException, IllegalAccessException {
        var deck = new Deck();
        deck.addCard(new Card(Constant.CardNumber.Ace, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Ace, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Two, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Ace, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Ace, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Ace, Constant.CardType.Spade));
        var baccarat = new Baccarat(deck, 6);

        baccarat.dealCards();
        var f = baccarat.getClass().getDeclaredField("dealerCards");
        var f2 = baccarat.getClass().getDeclaredField("playerCards");
        f.setAccessible(true);
        f2.setAccessible(true);
        var dealerCards = (List<Card>) f.get(baccarat);
        var playerCards = (List<Card>) f2.get(baccarat);
        Assert.assertEquals(2, dealerCards.size());
        Assert.assertEquals(3, playerCards.size());
    }

    @Test
    public void testBaccaratDealCardPlayerLowerThanThreeAndThirdCardIsEight() throws NoSuchFieldException, IllegalAccessException {
        var deck = new Deck();
        deck.addCard(new Card(Constant.CardNumber.Ace, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Ace, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Two, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Ace, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Eight, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Ace, Constant.CardType.Spade));
        var baccarat = new Baccarat(deck, 6);

        baccarat.dealCards();
        var f = baccarat.getClass().getDeclaredField("dealerCards");
        var f2 = baccarat.getClass().getDeclaredField("playerCards");
        f.setAccessible(true);
        f2.setAccessible(true);
        var dealerCards = (List<Card>) f.get(baccarat);
        var playerCards = (List<Card>) f2.get(baccarat);
        Assert.assertEquals(3, dealerCards.size());
        Assert.assertEquals(3, playerCards.size());
    }

    @Test
    public void testBaccaratDealCardPlayerLowerThanSix() throws NoSuchFieldException, IllegalAccessException {
        var deck = new Deck();
        deck.addCard(new Card(Constant.CardNumber.Ace, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Four, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Two, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Ace, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Seven, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Ace, Constant.CardType.Spade));
        var baccarat = new Baccarat(deck, 6);

        baccarat.dealCards();
        var f = baccarat.getClass().getDeclaredField("dealerCards");
        var f2 = baccarat.getClass().getDeclaredField("playerCards");
        f.setAccessible(true);
        f2.setAccessible(true);
        var dealerCards = (List<Card>) f.get(baccarat);
        var playerCards = (List<Card>) f2.get(baccarat);
        Assert.assertEquals(2, dealerCards.size());
        Assert.assertEquals(3, playerCards.size());
    }

    @Test
    public void testBaccaratDealCardPlayerBiggerThanSix() throws NoSuchFieldException, IllegalAccessException {
        var deck = new Deck();
        deck.addCard(new Card(Constant.CardNumber.Two, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Four, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Two, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Ace, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Eight, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Ace, Constant.CardType.Spade));
        var baccarat = new Baccarat(deck, 6);

        baccarat.dealCards();
        var f = baccarat.getClass().getDeclaredField("dealerCards");
        var f2 = baccarat.getClass().getDeclaredField("playerCards");
        f.setAccessible(true);
        f2.setAccessible(true);
        var dealerCards = (List<Card>) f.get(baccarat);
        var playerCards = (List<Card>) f2.get(baccarat);
        Assert.assertEquals(3, dealerCards.size());
        Assert.assertEquals(2, playerCards.size());
    }

    @Test
    public void testBaccaratDealCardDealerEqualFive() throws NoSuchFieldException, IllegalAccessException {
        var deck = new Deck();
        deck.addCard(new Card(Constant.CardNumber.Ace, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Ace, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Two, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Three, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Eight, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Ace, Constant.CardType.Spade));
        var baccarat = new Baccarat(deck, 6);

        baccarat.dealCards();
        var f = baccarat.getClass().getDeclaredField("dealerCards");
        var f2 = baccarat.getClass().getDeclaredField("playerCards");
        f.setAccessible(true);
        f2.setAccessible(true);
        var dealerCards = (List<Card>) f.get(baccarat);
        var playerCards = (List<Card>) f2.get(baccarat);
        Assert.assertEquals(3, dealerCards.size());
        Assert.assertEquals(3, playerCards.size());
    }

    @Test
    public void testBaccaratDealCardBothBiggerThanSix() throws NoSuchFieldException, IllegalAccessException {
        var deck = new Deck();
        deck.addCard(new Card(Constant.CardNumber.Ace, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Five, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Ace, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Five, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Eight, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Ace, Constant.CardType.Spade));
        var baccarat = new Baccarat(deck, 6);

        baccarat.dealCards();
        var f = baccarat.getClass().getDeclaredField("dealerCards");
        var f2 = baccarat.getClass().getDeclaredField("playerCards");
        f.setAccessible(true);
        f2.setAccessible(true);
        var dealerCards = (List<Card>) f.get(baccarat);
        var playerCards = (List<Card>) f2.get(baccarat);
        Assert.assertEquals(2, dealerCards.size());
        Assert.assertEquals(2, playerCards.size());
    }

    @Test
    public void testBaccaratDealCardPlayerBiggerThanSixAndDealerLessThanFive() throws NoSuchFieldException, IllegalAccessException {
        var deck = new Deck();
        deck.addCard(new Card(Constant.CardNumber.Ace, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Five, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Ace, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Four, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Eight, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Ace, Constant.CardType.Spade));
        var baccarat = new Baccarat(deck, 6);

        baccarat.dealCards();
        var f = baccarat.getClass().getDeclaredField("dealerCards");
        var f2 = baccarat.getClass().getDeclaredField("playerCards");
        f.setAccessible(true);
        f2.setAccessible(true);
        var dealerCards = (List<Card>) f.get(baccarat);
        var playerCards = (List<Card>) f2.get(baccarat);
        Assert.assertEquals(3, dealerCards.size());
        Assert.assertEquals(2, playerCards.size());
    }

    @Test
    public void testBaccaratBetAndSettleGame() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        var deck = new Deck();
        deck.addCard(new Card(Constant.CardNumber.Ace, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Eight, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Ace, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Eight, Constant.CardType.Spade));
        var baccarat = baccaratBuilder.create(true);
        var field = baccarat.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredField("deck");
        field.setAccessible(true);
        field.set(baccarat, deck);
        dataManager.saveUser(user);
        liveUser.setRoomId(room.getId());
        baccarat.setOwner(liveUser);
        var userA = createTester(200, "name");
        userDaoService.save(userA);
        baccarat.setPayRate(new BigDecimal("0.95"));
        baccarat.beginGameLoop(1000);
        baccarat.setDelayFinishTime(1000);
        Assert.assertEquals(Constant.GameStatus.BETTING, baccarat.getGameStatus());
        Pair<Constant.ResultCode, Long> betResult = baccarat.bet(userA, 100, Game.BetType.BACCARAT_DRAW);
        userA.setGameCoin(userA.getGameCoin() - betResult.getRight());
        Assert.assertEquals(Constant.ResultCode.SUCCESS, betResult.getLeft());
        betResult = baccarat.bet(userA, 100, Game.BetType.BACCARAT_DEALER);
        userA.setGameCoin(userA.getGameCoin() - betResult.getRight());
        Assert.assertEquals(Constant.ResultCode.SUCCESS,betResult.getLeft());
        dataManager.saveUser(userA);
        Thread.sleep(1500);
        userA = userDaoService.findById(userA.getId());
        Assert.assertEquals(Constant.GameStatus.SETTLING, baccarat.getGameStatus());
        Assert.assertEquals(Game.BetType.BACCARAT_DRAW, baccarat.getGameResult().get(0));
        Assert.assertEquals(860, userA.getGameCoin());
        forceStop(baccarat);
    }
}
