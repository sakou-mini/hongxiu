package com.donglaistd.jinli.processors.handler.game;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Game;
import com.donglaistd.jinli.builder.LonghuBuilder;
import com.donglaistd.jinli.database.dao.RoomDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.util.DataManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class StartGameRequestHandlerTest extends BaseTest {
    @Autowired
    StartGameRequestHandler handler;

    @Autowired
    LonghuBuilder longhuBuilder;

    @Autowired
    private RoomDaoService roomDaoService;
    @Autowired
    private UserDaoService userDaoService;

    @Test

    public void TestStartGame() throws InterruptedException {
        var longhu = longhuBuilder.create();
        var gameBuilder = Game.GameRequest.newBuilder();
        var request = Game.StartGameRequest.newBuilder();
        gameBuilder.setStartGameRequest(request);
        liveUser.setPlayingGameId(longhu.getGameId());
        longhu.setOwner(liveUser);
        longhu.setBettingTime(1000);
        dataManager.saveLiveUser(liveUser);
        Assert.assertEquals(Constant.GameStatus.PAUSED, longhu.getGameStatus());
        handler.handle(context, gameBuilder.build(), longhu);
        User user1 = userBuilder.createUser("", "", "");
        user1.setGameCoin(100);
        dataManager.saveUser(user1);
        DataManager.addGame(longhu);
        longhu.bet(user1, 100, Game.BetType.LONG);
        Assert.assertEquals(Constant.GameStatus.BETTING, longhu.getGameStatus());
        Assert.assertEquals(1, longhu.getBetAmountMap().size());
        Thread.sleep(1500);
        Assert.assertEquals(0, longhu.getBetAmountMap().size());
        forceStop(longhu);
    }
}
