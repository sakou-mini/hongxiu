package com.donglaistd.jinli.processors.handler.game;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.builder.LonghuBuilder;
import com.donglaistd.jinli.builder.RedBlackBuilder;
import com.donglaistd.jinli.database.dao.RoomDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.*;
import com.donglaistd.jinli.util.DataManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.logging.Logger;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DealtCardsRequestHandlerTest extends BaseTest {
    private static final Logger logger = Logger.getLogger(DealtCardsRequestHandlerTest.class.getName());
    @Autowired
    RedBlackBuilder redBlackBuilder;
    @Autowired
    LonghuBuilder longhuBuilder;


    @Autowired
    private RoomDaoService roomDaoService;
    @Autowired
    private UserDaoService userDaoService;

    public User creteUser(String name, int coin, int id) {
        User user = userBuilder.createUser(name + id, name + id, "admin" + id);
        user.setGameCoin(coin);
        userDaoService.save(user);
        return user;
    }

    @Test
    public void DealtCardsRedBlackTest() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        userDaoService.save(user);
        var redBlack = redBlackBuilder.create(true);
        var field2 = redBlack.getClass().getDeclaredField("minimalDeckCardRequest");
        field2.setAccessible(true);
        field2.set(redBlack, 5);
        redBlack.setGameStatus(Constant.GameStatus.PAUSED);
        redBlack.setPayRate(new BigDecimal("0.95"));

        liveUser.setPlayingGameId(redBlack.getGameId());
        DataManager.roomMap.put(room.getId(), room);
        redBlack.setOwner(liveUser);
        redBlack.beginGameLoop(1000);
        redBlack.setDelayFinishTime(500);

        Thread.sleep(2000);
        var reply = redBlack.getDeckDealt();
        Assert.assertEquals(40, reply.getCardsAmount());
        Assert.assertEquals(6, reply.getDealtsAmount());
        Thread.sleep(1200);
        reply = redBlack.getDeckDealt();
        Assert.assertEquals(34, reply.getCardsAmount());
        Assert.assertEquals(12, reply.getDealtsAmount());

        forceStop(redBlack);
    }

    @Test
    public void DealtCardsBaccaratTest() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        var longhu = longhuBuilder.create();

        var deck = new Deck();
        deck.addCard(new Card(Constant.CardNumber.Two, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Two, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Two, Constant.CardType.Spade));
        deck.addCard(new Card(Constant.CardNumber.Two, Constant.CardType.Spade));

        deck.addCard(new Card(Constant.CardNumber.Two, Constant.CardType.Club));
        deck.addCard(new Card(Constant.CardNumber.Two, Constant.CardType.Club));
        deck.addCard(new Card(Constant.CardNumber.Two, Constant.CardType.Club));
        deck.addCard(new Card(Constant.CardNumber.Two, Constant.CardType.Club));

        deck.addCard(new Card(Constant.CardNumber.Two, Constant.CardType.Diamond));
        deck.addCard(new Card(Constant.CardNumber.Two, Constant.CardType.Diamond));
        deck.addCard(new Card(Constant.CardNumber.Two, Constant.CardType.Diamond));
        deck.addCard(new Card(Constant.CardNumber.Two, Constant.CardType.Diamond));

        deck.addCard(new Card(Constant.CardNumber.Two, Constant.CardType.Heart));
        deck.addCard(new Card(Constant.CardNumber.Two, Constant.CardType.Heart));
        deck.addCard(new Card(Constant.CardNumber.Two, Constant.CardType.Heart));
        deck.addCard(new Card(Constant.CardNumber.Two, Constant.CardType.Heart));

        var field = longhu.getClass().getSuperclass().getSuperclass().getDeclaredField("deck");
        field.setAccessible(true);
        field.set(longhu, deck);
        var min = longhu.getClass().getDeclaredField("minimalDeckCardRequest");
        min.setAccessible(true);
        min.set(longhu, 2);

        liveUser.setRoomId(room.getId());
        liveUser.setLiveStatus(Constant.LiveStatus.ONLINE);
        liveUser.setLiveUrl("");
        liveUser.setPlayingGameId(longhu.getGameId());
        DataManager.roomMap.put(room.getId(), room);
        longhu.setOwner(liveUser);
        longhu.setPayRate(new BigDecimal("0.95"));
        longhu.beginGameLoop(1000);
        longhu.setDelayFinishTime(1000);
        Thread.sleep(2500);
        var reply = longhu.getDeckDealt();
        Assert.assertEquals(12, reply.getCardsAmount());
        Assert.assertEquals(2, reply.getDealtsAmount());

        forceStop(longhu);
    }

}
