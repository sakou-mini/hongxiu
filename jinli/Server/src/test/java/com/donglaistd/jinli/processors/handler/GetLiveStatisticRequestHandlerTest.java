package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.builder.DeckBuilder;
import com.donglaistd.jinli.database.dao.LiveUserDaoService;
import com.donglaistd.jinli.database.dao.RoomDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.game.Longhu;
import com.donglaistd.jinli.processors.handler.statistic.GetLiveStatisticRequestHandler;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import static com.donglaistd.jinli.Constant.ResultCode.NOT_LIVE_USER;
import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;
import static com.donglaistd.jinli.util.DataManager.roomMap;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GetLiveStatisticRequestHandlerTest extends BaseTest {
    @Autowired
    private GetLiveStatisticRequestHandler handler;
    @Autowired
    LiveUserDaoService liveUserDaoService;
    @Autowired
    private RoomDaoService roomDaoService;
    @Autowired
    private UserDaoService userDaoService;

    @Test
    public void testHandleFail() {
        user.setLiveUserId(null);
        dataManager.saveUser(user);
        Jinli.JinliMessageRequest.Builder request = Jinli.JinliMessageRequest.newBuilder();
        Jinli.GetLiveStatisticRequest.Builder builder = Jinli.GetLiveStatisticRequest.newBuilder();
        Assert.assertNull(user.getLiveUserId());
        Jinli.JinliMessageReply handle = handler.handle(context, request.setGetLiveStatisticRequest(builder).build());
        Assert.assertEquals(NOT_LIVE_USER, handle.getResultCode());
    }

    @Test

    @Rollback
    public void testHandleSuccess() {
        var room = new Room();
        room.setRoomTitle("sunny");
        room.setDescription("sunny_Description");
        room.initPlatformRoomData(Constant.PlatformType.PLATFORM_JINLI,user.getId(),0);
        roomDaoService.save(room);
        user.setDisplayName("sunny");
        userDaoService.save(user);
        liveUser.setLiveStatus(Constant.LiveStatus.ONLINE);
        room.setLiveUserId(liveUser.getId());
        Longhu longhu = new Longhu(DeckBuilder.getNoJokerDeck(4), 2);
        longhu.setOwner(liveUser);
        liveUser.setPlayingGameId(longhu.getGameId());
        user.setLiveUserId(liveUser.getId());
        roomMap.put(room.getId(), room);
        dataManager.saveUser(user);

        Jinli.JinliMessageRequest.Builder request = Jinli.JinliMessageRequest.newBuilder();
        Jinli.GetLiveStatisticRequest.Builder builder = Jinli.GetLiveStatisticRequest.newBuilder();
        Jinli.JinliMessageReply handle = handler.handle(context, request.setGetLiveStatisticRequest(builder.build()).build());
        Assert.assertEquals(SUCCESS, handle.getResultCode());
    }
}
