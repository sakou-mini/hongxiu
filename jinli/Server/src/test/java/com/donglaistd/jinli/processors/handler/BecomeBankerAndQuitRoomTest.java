package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.builder.BullBullBuilder;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.game.BullBull;
import com.donglaistd.jinli.processors.handler.game.BecomeBankerRequestHandler;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.util.DataManager;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;

public class BecomeBankerAndQuitRoomTest extends BaseTest {
    @Autowired
    BullBullBuilder bullBullBuilder;
    @Autowired
    BecomeBankerRequestHandler becomeBankerRequestHandler;
    @Autowired
    EnterRoomRequestHandler enterRoomRequestHandler;
    @Autowired
    QuitRoomRequestHandler quitRoomRequestHandler;
    @Autowired
    UserDaoService userDaoService;

    @Override
    public void setUp() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        super.setUp();
        EventPublisher.isEnabled = new AtomicBoolean(true);
    }

    @Test
    public void test() {
        BullBull game = bullBullBuilder.create(true);
        liveUser.setPlayingGameId(game.getGameId());
        dataManager.saveLiveUser(liveUser);
        game.setOwner(liveUser);
        DataManager.addGame(game);

        Room room = DataManager.roomMap.get(this.room.getId());
        room.addAudience(user);
        game.startGame();
        user.setGameCoin(20000);
        dataManager.saveUser(user);

        //1.joinRoom
        Jinli.JinliMessageRequest.Builder requestBuilder = Jinli.JinliMessageRequest.newBuilder();
        requestBuilder.setEnterRoomRequest(Jinli.EnterRoomRequest.newBuilder().setRoomId(room.getId()));
        Jinli.JinliMessageReply reply = enterRoomRequestHandler.handle(context, requestBuilder.build());
        Assert.assertEquals(SUCCESS, reply.getResultCode());

        //2.apply becomeBanker
        Constant.ResultCode resultCode = game.addWaitingBanker(user, 10000);
        Assert.assertEquals(SUCCESS, resultCode);

        var banker1 = userDaoService.findById(user.getId());
        Assert.assertEquals(10000, banker1.getGameCoin());

        //3.quit Room
        requestBuilder.clear();
        Jinli.QuitRoomRequest.Builder quitRoomRequest = Jinli.QuitRoomRequest.newBuilder().setRoomId(room.getId());
        requestBuilder.setQuitRoomRequest(quitRoomRequest);
        reply = quitRoomRequestHandler.handle(context, requestBuilder.build());

        Assert.assertEquals(SUCCESS, reply.getResultCode());
        banker1 = userDaoService.findById(banker1.getId());

        Assert.assertEquals(20000, banker1.getGameCoin());
    }
}
