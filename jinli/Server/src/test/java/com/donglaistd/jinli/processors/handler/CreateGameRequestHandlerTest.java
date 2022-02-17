package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.game.Longhu;
import com.donglaistd.jinli.util.DataManager;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.donglaistd.jinli.Constant.GameType.LONGHU;
import static com.donglaistd.jinli.processors.handler.MessageHandler.USER_KEY;

public class CreateGameRequestHandlerTest extends BaseTest {
    @Autowired
    private CreateGameRequestHandler createGameRequestHandler;
    @Autowired
    private UserDaoService userDaoService;

    @Test
    public void TestCreateLonghu() {
        var request = Jinli.JinliMessageRequest.newBuilder();
        var createGameRequest = Jinli.CreateGameRequest.newBuilder();
        createGameRequest.setGameType(LONGHU);
        request.setCreateGameRequest(createGameRequest);
        var reply = createGameRequestHandler.doHandle(context, request.build(), user);
        User userFromChannel = userDaoService.findById(context.channel().attr(USER_KEY).get());
        Assert.assertEquals(Constant.ResultCode.SUCCESS, reply.getResultCode());
        var playingGame = DataManager.findGame(dataManager.findLiveUser(userFromChannel.getLiveUserId()).getPlayingGameId());
        Assert.assertEquals(Longhu.class, playingGame.getClass());
    }
}
