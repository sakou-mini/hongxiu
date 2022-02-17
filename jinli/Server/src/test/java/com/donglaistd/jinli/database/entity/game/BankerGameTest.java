package com.donglaistd.jinli.database.entity.game;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Game;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.builder.DeckBuilder;
import com.donglaistd.jinli.database.dao.RoomDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.service.EventPublisher;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.donglaistd.jinli.Constant.LiveStatus.ONLINE;
import static com.donglaistd.jinli.Constant.ResultCode.*;

public class BankerGameTest extends BaseTest {

    @Autowired
    private RoomDaoService roomDaoService;

    BankerGame game = new BankerGame() {

        private String gameId;

        @Override
        public String getGameId() {
            return gameId;
        }

        @Override
        protected boolean checkGameBetLimit(User user, long betAmount, Game.BetType betType) {
            return false;
        }

        @Override
        protected int getMinimalDeckCardRequest() {
            return 0;
        }

        @Override
        public Constant.GameType getGameType() {
            return Constant.GameType.LONGHU;
        }

        @Override
        protected List<Game.BetType> getGameResult() {
            return null;
        }

        @Override
        protected void dealCards() {

        }

        @Override
        protected void processGameResult(List<Game.BetType> gameResults) {
            bankerCoinAmount -= 150;
        }

        @Override
        public List<? extends Jinli.GameResult> getCardHistory() {
            return null;
        }

        @Override
        public int getBankerMinimalCoin() {
            return 100;
        }

        @Override
        public int getBankerContinueCoin() {
            return 51;
        }

        @Override
        protected int getSystemBankerCoin() {
            return 0;
        }
    };

    @Autowired
    UserDaoService userDaoService;

    @Test
    public void TestSetUpBanker() throws InterruptedException {
        EventPublisher.isEnabled = new AtomicBoolean(true);
        user.setGameCoin(200);
        userDaoService.save(user);
        LiveUser liveUser =  liveUserBuilder.create(user.getId(),ONLINE, Constant.PlatformType.PLATFORM_JINLI);
        Room room = roomBuilder.create(liveUser.getId(), user.getId(), "title", "des", "");

        liveUser.setRoomId(room.getId());
        dataManager.saveLiveUser(liveUser);

        user.setLiveUserId(liveUser.getId());
        dataManager.saveUser(user);
        game.setOwner(liveUser);
        Assert.assertEquals(NOT_ENOUGH_GAMECOIN, game.addWaitingBanker(user, 201));
        Assert.assertEquals(SUCCESS, game.addWaitingBanker(user, 100));
        user = userDaoService.findById(user.getId());
        Assert.assertEquals(NOT_ENOUGH_GAMECOIN, game.addWaitingBanker(user, 101));
        Assert.assertEquals(ALREADY_IN_BANKER_QUEUE, game.addWaitingBanker(user, 100));
    }

    @Test
    public void TestCancelBanker() {

        LiveUser liveUser =  liveUserBuilder.create(user.getId(),ONLINE, Constant.PlatformType.PLATFORM_JINLI);
        liveUser.setRoomId(room.getId());
        dataManager.saveRoom(room);

        user.setGameCoin(200);
        user.setLiveUserId(liveUser.getId());
        dataManager.saveUser(user);
        game.setOwner(liveUser);
        Assert.assertEquals(SUCCESS, game.addWaitingBanker(user, 100));
        Assert.assertEquals(SUCCESS, game.quitBanker(user,false));
    }

    @Test
    public void TestBankerWaitingList() {

        User user2 = createTester(200, "user2");
        userDaoService.save(user2);
        LiveUser liveUser = liveUserBuilder.create(user.getId(),ONLINE, Constant.PlatformType.PLATFORM_JINLI);
        liveUser.setRoomId(room.getId());

        user.setGameCoin(200);
        user.setLiveUserId(liveUser.getId());
        dataManager.saveUser(user);

        game.setOwner(liveUser);
        dataManager.saveUser(user2);
        Assert.assertEquals(SUCCESS, game.addWaitingBanker(user, 100));
        Assert.assertEquals(SUCCESS, game.addWaitingBanker(user2, 100));
        Assert.assertEquals(2, game.getBankerInfoMessage().getWaitingBankersList().size());
    }

    @Test
    public void TestBankerChange() throws InterruptedException {
        User user = userBuilder.createUser("name", "name","" );
        user.setGameCoin(200);
        user.setOnline(true);
        user = userDaoService.save(user);
        liveUser.setLiveStatus(Constant.LiveStatus.ONLINE);
        dataManager.saveLiveUser(liveUser);
        User user2  = userBuilder.createUser("name2", "name2","" );
        user2.setGameCoin(200);
        user2.setOnline(true);
        user2 = dataManager.saveUser(user2);
        game.setOwner(liveUser);
        game.addWaitingBanker(user, 200);
        game.addWaitingBanker(user2, 200);
        game.deck = DeckBuilder.getNoJokerDeck(1);
        game.beginGameLoop(1000);
        Assert.assertSame(user, game.banker);
        Thread.sleep(1500);
        Assert.assertSame(user2, game.banker);
        forceStop(game);
    }
}
