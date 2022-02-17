package com.donglaistd.jinli.processors.handler.connectLive;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.RoomManagement;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.util.DataManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.donglaistd.jinli.Constant.ResultCode.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MuteAllConnectLiveRequestHandlerTest extends BaseTest {
    @Autowired
    private DataManager dataManager;
    @Autowired
    private MuteAllConnectLiveRequestHandler muteAllConnectLiveRequestHandler;
    @Autowired
    private UnMuteAllConnectLiveRequestHandler unMuteAllConnectLiveRequestHandler;

    @Test
    public void testMuteAll() {
        String liveUserId = liveUser.getId();
        liveUser.setUserId(user.getId());
        user.setLiveUserId(liveUserId);
        liveUser.setRoomId(room.getId());
        dataManager.saveUser(user);
        dataManager.saveLiveUser(liveUser);
        User audience = userBuilder.createUser("test_ audience", "audience", "//audience", "audience", true);
        room.addAudience( audience);

        dataManager.saveRoom(room);

        RoomManagement.RoomManagementRequest.Builder builder = RoomManagement.RoomManagementRequest.newBuilder();
        RoomManagement.MuteAllConnectLiveRequest.Builder request = RoomManagement.MuteAllConnectLiveRequest.newBuilder();
        var pair = muteAllConnectLiveRequestHandler.handle(context, builder.setMuteAllConnectLiveRequest(request.build()).build(), audience, room);
        Assert.assertEquals(INSUFFICIENT_PERMISSIONS, pair.getRight());

        room.setMuteAll(true);
        room.setLiveUserId(liveUserId);
        pair = muteAllConnectLiveRequestHandler.handle(context, builder.setMuteAllConnectLiveRequest(request.build()).build(), user, room);
        Assert.assertEquals(ILLEGAL_OPERATION, pair.getRight());


        room.addAdministrator(audience.getId());
        room.setMuteAll(false);
        pair = muteAllConnectLiveRequestHandler.handle(context, builder.setMuteAllConnectLiveRequest(request.build()).build(), audience, room);
        Assert.assertEquals(SUCCESS, pair.getRight());
    }

    @Test
    public void testUnMuteAll() {
        RoomManagement.RoomManagementRequest.Builder builder = RoomManagement.RoomManagementRequest.newBuilder();
        RoomManagement.MuteAllConnectLiveRequest.Builder request = RoomManagement.MuteAllConnectLiveRequest.newBuilder();
        String liveUserId = liveUser.getId();
        liveUser.setUserId(user.getId());
        user.setLiveUserId(liveUserId);
        liveUser.setRoomId(room.getId());
        dataManager.saveUser(user);
        dataManager.saveLiveUser(liveUser);
        User audience =  userBuilder.createUser("test_ audience", "audience", "//audience", "audience", true);
        room.addAudience( audience);
        dataManager.saveRoom(room);

        room.addAdministrator(audience.getId());
        var pair = muteAllConnectLiveRequestHandler.handle(context, builder.setMuteAllConnectLiveRequest(request.build()).build(), audience, room);
        Assert.assertEquals(SUCCESS, pair.getRight());
        Assert.assertTrue(room.getMuteAll());


        room.setMuteAll(true);
        RoomManagement.UnMuteAllConnectLiveRequest.Builder unMuteAll = RoomManagement.UnMuteAllConnectLiveRequest.newBuilder();
        pair = unMuteAllConnectLiveRequestHandler.handle(context, builder.setUnMuteAllConnectLiveRequest(unMuteAll.build()).build(), audience, room);
        Assert.assertEquals(SUCCESS, pair.getRight());
        Assert.assertFalse(room.getMuteAll());
    }
}
