package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.builder.GameBuilder;
import com.donglaistd.jinli.database.dao.RoomDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.game.Baccarat;
import com.donglaistd.jinli.database.entity.game.BaseGame;
import com.donglaistd.jinli.database.entity.game.Longhu;
import com.donglaistd.jinli.exception.JinliException;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.util.DataManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.donglaistd.jinli.Constant.GameType.BACCARAT;
import static com.donglaistd.jinli.Constant.GameType.LONGHU;
import static com.donglaistd.jinli.processors.handler.MessageHandler.USER_KEY;

public class SwitchGameRequestHandlerTest extends BaseTest {
    @Autowired
    private SwitchGameRequestHandler switchGameRequestHandler;
    @Autowired
    GameBuilder gameBuilder;
    @Autowired
    private UserDaoService userDaoService;

    @Before
    public void setup() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        super.setUp();
        DataManager.cleanGame();
    }

    @Test
    public void TestSwitchFromLonghuToBaccarat() throws JinliException, InterruptedException {
        EventPublisher.isEnabled = new AtomicBoolean(true);
        var user = userDaoService.findById(context.channel().attr(USER_KEY).get());
        var game = gameBuilder.createGame(LONGHU, liveUser);
        DataManager.addGame(game);
        liveUser.setPlayingGameId(game.getGameId());
        user.setLiveUserId(liveUser.getId());
        dataManager.saveUser(user);
        dataManager.saveLiveUser(liveUser);
        game.beginGameLoop(1000);
        game.setDelayFinishTime(0);
        var request = Jinli.JinliMessageRequest.newBuilder();
        var switchGameRequest = Jinli.SwitchGameRequest.newBuilder();
        switchGameRequest.setGameType(BACCARAT);
        request.setSwitchGameRequest(switchGameRequest);
        var reply = switchGameRequestHandler.handle(context, request.build());
        var playingGame = DataManager.findGame(liveUser.getPlayingGameId());
        Assert.assertEquals(Longhu.class, playingGame.getClass());
        Assert.assertEquals(Constant.ResultCode.SUCCESS, reply.getResultCode());
        Thread.sleep(1500);
        String playingGameId = DataManager.findAllGame().stream().filter(g -> g instanceof BaseGame).map(g -> (BaseGame) g).iterator().next().getOwner().getPlayingGameId();
        Assert.assertEquals(Baccarat.class, DataManager.findGame(playingGameId).getClass());
        forceStop(game);
    }

    @Test
    public void TestSwitchWillExpectCorrectGame() throws JinliException, InterruptedException {
        EventPublisher.isEnabled = new AtomicBoolean(true);
        var user2 = createTester(0, "user2");
        liveUser.setUserId(user.getId());
        LiveUser liveUser2 = liveUserBuilder.create(user2.getId(), Constant.LiveStatus.ONLINE, user2.getPlatformType());

        Room room2 = roomBuilder.create(liveUser2.getId(), user2.getId(), "", "", "");
        liveUser2.setRoomId(room2.getId());

        var game = gameBuilder.createGame(LONGHU, liveUser);
        var game2 = gameBuilder.createGame(LONGHU, liveUser2);
        liveUser.setPlayingGameId(game.getGameId());
        liveUser2.setPlayingGameId(game2.getGameId());
        user2.setLiveUserId(liveUser2.getId());

        dataManager.saveUser(user);
        dataManager.saveUser(user2);
        dataManager.saveLiveUser(liveUser);
        dataManager.saveLiveUser(liveUser2);
        DataManager.addGame(game);
        DataManager.addGame(game2);
        DataManager.roomMap.put(room.getId(), room);
        DataManager.roomMap.put(room2.getId(), room2);

        game.beginGameLoop(1200);
        game.setDelayFinishTime(0);
        game2.beginGameLoop(900);
        game2.setDelayFinishTime(0);
        var request = Jinli.JinliMessageRequest.newBuilder();
        var switchGameRequest = Jinli.SwitchGameRequest.newBuilder();
        switchGameRequest.setGameType(BACCARAT);
        request.setSwitchGameRequest(switchGameRequest);
        var reply = switchGameRequestHandler.handle(context, request.build());
        var playingGame = DataManager.findGame(liveUser.getPlayingGameId());
        Assert.assertEquals(Longhu.class, playingGame.getClass());
        Assert.assertEquals(Constant.ResultCode.SUCCESS, reply.getResultCode());
        Thread.sleep(1500);
        var playingGame2 = DataManager.findGame(liveUser.getPlayingGameId());
        Assert.assertEquals(Baccarat.class, playingGame2.getClass());
        Assert.assertEquals(Constant.GameStatus.BETTING, game2.getGameStatus());
        forceStop(game);
        forceStop(game2);
    }
}