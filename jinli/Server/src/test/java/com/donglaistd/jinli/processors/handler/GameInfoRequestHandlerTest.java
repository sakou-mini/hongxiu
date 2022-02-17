package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.builder.DeckBuilder;
import com.donglaistd.jinli.database.dao.LiveUserDaoService;
import com.donglaistd.jinli.database.dao.RoomDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.Card;
import com.donglaistd.jinli.database.entity.Deck;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.game.BullBull;
import com.donglaistd.jinli.database.entity.game.Longhu;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.donglaistd.jinli.Constant.CardNumber.*;
import static com.donglaistd.jinli.Constant.CardType.*;
import static com.donglaistd.jinli.util.DataManager.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GameInfoRequestHandlerTest extends BaseTest {

    @Autowired
    GameInfoRequestHandler gameInfoRequestHandler;

    @Autowired
    LiveUserDaoService liveUserDaoService;

    @Autowired
    UserDaoService userDaoService;
    @Autowired
    private RoomDaoService roomDaoService;

    @Test

    public void TestSuccessfulGetGameInfo() {
        var room = new Room();
        room.setRoomTitle("title");
        room.setDescription("Description");
        roomDaoService.save(room);
        liveUser.setUserId(user.getId());
        liveUser.setRoomId(room.getId());
        liveUser.setLiveUrl("liveurl");
        liveUser.setLiveStatus(Constant.LiveStatus.ONLINE);
        room.setLiveUserId(liveUser.getId());
        liveUserDaoService.save(liveUser);
        roomMap.put(room.getId(), room);
        dataManager.saveLiveUser(liveUser);

        var requestWrapper = Jinli.JinliMessageRequest.newBuilder();
        var request = Jinli.GameInfoRequest.newBuilder();
        request.setRoomId(room.getId());
        requestWrapper.setGameInfoRequest(request);

        var message = gameInfoRequestHandler.handle(context, requestWrapper.build());
        Assert.assertEquals(Constant.ResultCode.SUCCESS, message.getResultCode());
        Assert.assertEquals(room.getId(), message.getGameInfoReply().getRoomId());
    }

    @Test

    public void TestSuccessfulGetGameInfoWithGame() {
        var room = new Room();
        room.setRoomTitle("title");
        room.setDescription("Description");
        roomDaoService.save(room);
        liveUser.setUserId(user.getId());
        liveUser.setRoomId(room.getId());
        liveUser.setLiveUrl("liveurl");
        liveUser.setLiveStatus(Constant.LiveStatus.ONLINE);
        room.setLiveUserId(liveUser.getId());
        Longhu longhu = new Longhu(DeckBuilder.getNoJokerDeck(4), 2);
        longhu.setOwner(liveUser);
        liveUser.setPlayingGameId(longhu.getGameId());
        liveUserDaoService.save(liveUser);
        roomMap.put(room.getId(), room);
        addGame(longhu);
        dataManager.saveLiveUser(liveUser);

        var requestWrapper = Jinli.JinliMessageRequest.newBuilder();
        var request = Jinli.GameInfoRequest.newBuilder();
        request.setRoomId(room.getId());
        requestWrapper.setGameInfoRequest(request);

        var message = gameInfoRequestHandler.handle(context, requestWrapper.build());
        Assert.assertEquals(Constant.ResultCode.SUCCESS, message.getResultCode());
        Assert.assertEquals(room.getId(), message.getGameInfoReply().getRoomId());
        Assert.assertEquals(0, message.getGameInfoReply().getDealtCardCount());
        Assert.assertEquals(0, message.getGameInfoReply().getBetInfoCount());
        Assert.assertEquals(Constant.GameStatus.PAUSED, message.getGameInfoReply().getGameStatus());
    }

    @Test

    public void TestGetGameInfoFailed() {
        var requestWrapper = Jinli.JinliMessageRequest.newBuilder();
        var request = Jinli.GameInfoRequest.newBuilder();
        request.setRoomId("1");
        requestWrapper.setGameInfoRequest(request);
        gameInfoRequestHandler.handle(context, requestWrapper.build());
        Assert.assertEquals(Constant.ResultCode.ROOM_DOES_NOT_EXIST, gameInfoRequestHandler.resultCode);
    }

    @Test

    public void TestGetBullbullGameInfo() throws InterruptedException {
        liveUser.setUserId(user.getId());
        liveUser.setRoomId(room.getId());
        liveUser.setLiveUrl("liveurl");
        liveUser.setLiveStatus(Constant.LiveStatus.ONLINE);
        room.setLiveUserId(liveUser.getId());
        Deck deck = new Deck();
        addCard(deck, Spade);
        addCard(deck, Heart);
        addCard(deck, Club);
        addCard(deck, Diamond);
        var game = new BullBull(deck, 2);
        game.setOwner(liveUser);
        liveUser.setPlayingGameId(game.getGameId());
        liveUserDaoService.save(liveUser);
        addGame(game);
        dataManager.saveLiveUser(liveUser);

        var requestWrapper = Jinli.JinliMessageRequest.newBuilder();
        var request = Jinli.GameInfoRequest.newBuilder();
        request.setRoomId(room.getId());
        requestWrapper.setGameInfoRequest(request);

        game.beginGameLoop(1000);
        game.setDelayFinishTime(1000);
        game.setInitShowNum(3);

        var message = gameInfoRequestHandler.handle(context, requestWrapper.build());
        Assert.assertEquals(Constant.ResultCode.SUCCESS, message.getResultCode());
        Assert.assertEquals(room.getId(), message.getGameInfoReply().getRoomId());
        Assert.assertEquals(25, message.getGameInfoReply().getDealtCardCount());
        Assert.assertEquals(0, message.getGameInfoReply().getBetInfoCount());
        Assert.assertEquals(Constant.GameStatus.BETTING, message.getGameInfoReply().getGameStatus());
        Assert.assertEquals(3, message.getGameInfoReply().getGameDetail().getBullBullCardResult().getBullbullCardShow().getBankerCardsCount());
        Assert.assertEquals(3, message.getGameInfoReply().getGameDetail().getBullBullCardResult().getBullbullCardShow().getSpadeCardsCount());
        Assert.assertEquals(3, message.getGameInfoReply().getGameDetail().getBullBullCardResult().getBullbullCardShow().getHeartCardsCount());
        Assert.assertEquals(3, message.getGameInfoReply().getGameDetail().getBullBullCardResult().getBullbullCardShow().getClubCardsCount());
        Assert.assertEquals(3, message.getGameInfoReply().getGameDetail().getBullBullCardResult().getBullbullCardShow().getDiamondCardsCount());
        Assert.assertEquals(Seven, message.getGameInfoReply().getGameDetail().getBullBullCardResult().getBullbullCardShow().getBankerCards(0).getCardValue());
        Assert.assertEquals(Club, message.getGameInfoReply().getGameDetail().getBullBullCardResult().getBullbullCardShow().getBankerCards(0).getCardType());
        Assert.assertEquals(Ace, message.getGameInfoReply().getGameDetail().getBullBullCardResult().getBullbullCardShow().getSpadeCards(0).getCardValue());
        Assert.assertEquals(Spade, message.getGameInfoReply().getGameDetail().getBullBullCardResult().getBullbullCardShow().getSpadeCards(0).getCardType());
        Assert.assertEquals(Six, message.getGameInfoReply().getGameDetail().getBullBullCardResult().getBullbullCardShow().getHeartCards(0).getCardValue());
        Assert.assertEquals(Spade, message.getGameInfoReply().getGameDetail().getBullBullCardResult().getBullbullCardShow().getHeartCards(0).getCardType());
        Assert.assertEquals(Four, message.getGameInfoReply().getGameDetail().getBullBullCardResult().getBullbullCardShow().getClubCards(0).getCardValue());
        Assert.assertEquals(Heart, message.getGameInfoReply().getGameDetail().getBullBullCardResult().getBullbullCardShow().getClubCards(0).getCardType());
        Assert.assertEquals(Two, message.getGameInfoReply().getGameDetail().getBullBullCardResult().getBullbullCardShow().getDiamondCards(0).getCardValue());
        Assert.assertEquals(Club, message.getGameInfoReply().getGameDetail().getBullBullCardResult().getBullbullCardShow().getDiamondCards(0).getCardType());
        Thread.sleep(1500);
        message = gameInfoRequestHandler.handle(context, requestWrapper.build());
        Assert.assertEquals(Constant.ResultCode.SUCCESS, message.getResultCode());
        Assert.assertEquals(room.getId(), message.getGameInfoReply().getRoomId());
        Assert.assertEquals(25, message.getGameInfoReply().getDealtCardCount());
        Assert.assertEquals(0, message.getGameInfoReply().getBetInfoCount());
        Assert.assertEquals(Constant.GameStatus.SETTLING, message.getGameInfoReply().getGameStatus());
        Assert.assertEquals(5, message.getGameInfoReply().getGameDetail().getBullBullCardResult().getBullbullCardShow().getBankerCardsCount());
        Assert.assertEquals(5, message.getGameInfoReply().getGameDetail().getBullBullCardResult().getBullbullCardShow().getSpadeCardsCount());
        Assert.assertEquals(5, message.getGameInfoReply().getGameDetail().getBullBullCardResult().getBullbullCardShow().getHeartCardsCount());
        Assert.assertEquals(5, message.getGameInfoReply().getGameDetail().getBullBullCardResult().getBullbullCardShow().getClubCardsCount());
        Assert.assertEquals(5, message.getGameInfoReply().getGameDetail().getBullBullCardResult().getBullbullCardShow().getDiamondCardsCount());
        Assert.assertEquals(Seven, message.getGameInfoReply().getGameDetail().getBullBullCardResult().getBullbullCardShow().getBankerCards(0).getCardValue());
        Assert.assertEquals(Club, message.getGameInfoReply().getGameDetail().getBullBullCardResult().getBullbullCardShow().getBankerCards(0).getCardType());
        Assert.assertEquals(Ace, message.getGameInfoReply().getGameDetail().getBullBullCardResult().getBullbullCardShow().getSpadeCards(0).getCardValue());
        Assert.assertEquals(Spade, message.getGameInfoReply().getGameDetail().getBullBullCardResult().getBullbullCardShow().getSpadeCards(0).getCardType());
        Assert.assertEquals(Six, message.getGameInfoReply().getGameDetail().getBullBullCardResult().getBullbullCardShow().getHeartCards(0).getCardValue());
        Assert.assertEquals(Spade, message.getGameInfoReply().getGameDetail().getBullBullCardResult().getBullbullCardShow().getHeartCards(0).getCardType());
        Assert.assertEquals(Four, message.getGameInfoReply().getGameDetail().getBullBullCardResult().getBullbullCardShow().getClubCards(0).getCardValue());
        Assert.assertEquals(Heart, message.getGameInfoReply().getGameDetail().getBullBullCardResult().getBullbullCardShow().getClubCards(0).getCardType());
        Assert.assertEquals(Two, message.getGameInfoReply().getGameDetail().getBullBullCardResult().getBullbullCardShow().getDiamondCards(0).getCardValue());
        Assert.assertEquals(Club, message.getGameInfoReply().getGameDetail().getBullBullCardResult().getBullbullCardShow().getDiamondCards(0).getCardType());
    }

    private void addCard(Deck deck, Constant.CardType cardType) {
        deck.addCard(new Card(Ace, cardType));
        deck.addCard(new Card(Constant.CardNumber.Two, cardType));
        deck.addCard(new Card(Constant.CardNumber.Three, cardType));
        deck.addCard(new Card(Constant.CardNumber.Four, cardType));
        deck.addCard(new Card(Constant.CardNumber.Five, cardType));
        deck.addCard(new Card(Constant.CardNumber.Six, cardType));
        deck.addCard(new Card(Constant.CardNumber.Seven, cardType));
    }
}
