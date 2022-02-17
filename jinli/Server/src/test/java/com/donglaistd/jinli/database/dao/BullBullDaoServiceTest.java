package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.builder.BullBullBuilder;
import com.donglaistd.jinli.builder.GameBuilder;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.game.BaseGame;
import com.donglaistd.jinli.exception.JinliException;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.logging.Logger;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BullBullDaoServiceTest extends BaseTest {
    private static final Logger logger = Logger.getLogger(BullBullDaoServiceTest.class.getName());

    @Autowired
    BullBullDaoService bullBullDaoService;
    @Autowired
    BullBullBuilder bullBullBuilder;
    @Autowired
    GameBuilder gameBuilder;

    @Test
    public void SaveAndQueryTest() throws JinliException {
        dataManager.saveRoom(room);
        room.setDisplayId(""+System.currentTimeMillis());
        dataManager.saveRoom(room);
        liveUser.setRoomId(room.getId());
        BaseGame bullBull = gameBuilder.createGame(Constant.GameType.NIUNIU, liveUser);
        Assert.assertEquals(bullBull.getGameType(), Constant.GameType.NIUNIU);
    }
}
