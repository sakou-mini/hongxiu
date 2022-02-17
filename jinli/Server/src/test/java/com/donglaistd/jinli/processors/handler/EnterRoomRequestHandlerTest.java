package com.donglaistd.jinli.processors.handler;


import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.builder.BullBullBuilder;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.game.BullBull;
import com.donglaistd.jinli.util.DataManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EnterRoomRequestHandlerTest extends BaseTest {
    @Autowired
    private EnterRoomRequestHandler enterRoomRequestHandler;
    @Autowired
    private BullBullBuilder bullBullBuilder;


    @Autowired
    UserDaoService userDaoService;

    @Test
    public void testEnterRoomDoesNotExist() {
        Jinli.JinliMessageRequest.Builder request = Jinli.JinliMessageRequest.newBuilder();
        Jinli.EnterRoomRequest.Builder builder = Jinli.EnterRoomRequest.newBuilder();
        Jinli.JinliMessageReply handle = enterRoomRequestHandler.handle(context, request.setEnterRoomRequest(builder.setRoomId("1").build()).build());
        Assert.assertEquals(Constant.ResultCode.ROOM_DOES_NOT_EXIST, handle.getResultCode());
    }

    @Test
    public void testEnterRoomRoomSuccess() {
        BullBull bullBull = bullBullBuilder.create(true);
        Room room = DataManager.roomMap.get(this.room.getId());
        liveUser.setLiveStatus(Constant.LiveStatus.ONLINE);
        bullBull.setOwner(liveUser);
        bullBull.setBettingTime(29000);
        liveUser.setPlayingGameId(bullBull.getGameId());
        room.setLiveUserId(liveUser.getId());
        DataManager.addGame(bullBull);
        dataManager.saveLiveUser(liveUser);

        Jinli.JinliMessageRequest.Builder request = Jinli.JinliMessageRequest.newBuilder();
        Jinli.EnterRoomRequest.Builder builder = Jinli.EnterRoomRequest.newBuilder();
        Jinli.JinliMessageReply handle = enterRoomRequestHandler.handle(context, request.setEnterRoomRequest(builder.setRoomId(room.getId()).build()).build());
        Assert.assertEquals(Constant.ResultCode.SUCCESS, handle.getResultCode());
    }

    @Test
    public void testEnterRoomFailedWithEmptyGame() {
        Jinli.JinliMessageRequest.Builder request = Jinli.JinliMessageRequest.newBuilder();
        Jinli.EnterRoomRequest.Builder builder = Jinli.EnterRoomRequest.newBuilder();
        Jinli.JinliMessageReply handle = enterRoomRequestHandler.handle(context, request.setEnterRoomRequest(builder.setRoomId(room.getId()).build()).build());
        Assert.assertEquals(Constant.ResultCode.SUCCESS, handle.getResultCode());
        Constant.GameType gameType = handle.getEnterRoomReply().getGameType();
        Assert.assertEquals(gameType, Constant.GameType.EMPTY);
    }

    @Test
    public void testEnterRoomAddAudienceList() {
        liveUser.setRoomId(room.getId());
        liveUser.setLiveStatus(Constant.LiveStatus.ONLINE);
        room.setLiveUserId(liveUser.getId());

        Jinli.JinliMessageRequest.Builder request = Jinli.JinliMessageRequest.newBuilder();
        Jinli.EnterRoomRequest.Builder builder = Jinli.EnterRoomRequest.newBuilder();
        Jinli.JinliMessageReply handle = enterRoomRequestHandler.handle(context, request.setEnterRoomRequest(builder.setRoomId(room.getId()).build()).build());
        Assert.assertEquals(Constant.ResultCode.SUCCESS, handle.getResultCode());
        Assert.assertEquals(1, room.getAllPlatformAudienceList().size());
    }
}
