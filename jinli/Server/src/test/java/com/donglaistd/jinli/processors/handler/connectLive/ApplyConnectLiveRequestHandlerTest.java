package com.donglaistd.jinli.processors.handler.connectLive;


import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.RoomManagement;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.util.DataManager;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.donglaistd.jinli.Constant.ResultCode.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplyConnectLiveRequestHandlerTest extends BaseTest {
    @Autowired
    private DataManager dataManager;
    @Autowired
    private ApplyConnectLiveRequestHandler applyConnectLiveRequestHandler;

    @Test
    public void testApplyConnectLiveSuccess() {
        liveUser.setUserId(user.getId());
        user.setLiveUserId(liveUser.getId());
        liveUser.setRoomId(room.getId());
        dataManager.saveUser(user);
        dataManager.saveLiveUser(liveUser);
        User audience = userBuilder.createUser("test_ audience", "audience", "//audience", "audience", true);
        audience.setVipType(Constant.VipType.SAINT);
        dataManager.saveUser(audience);
        room.addAudience(audience);
        dataManager.saveRoom(room);

        RoomManagement.RoomManagementRequest.Builder builder = RoomManagement.RoomManagementRequest.newBuilder();
        RoomManagement.ApplyConnectLiveRequest.Builder request = RoomManagement.ApplyConnectLiveRequest.newBuilder();
        var pair = applyConnectLiveRequestHandler.handle(context, builder.setApplyConnectLiveRequest(request.build()).build(), audience, room);
        Assert.assertEquals(SUCCESS,pair.getRight());
        Assert.assertEquals(1,room.getConnectLiveCodeSize());
    }

    @Test
    public void testApplyConnectLiveFail() {
        liveUser.setUserId(user.getId());
        user.setLiveUserId(liveUser.getId());
        liveUser.setRoomId(room.getId());
        dataManager.saveUser(user);
        dataManager.saveLiveUser(liveUser);
        User audience =  userBuilder.createUser("test_ audience", "audience", "//audience", "audience", true);
        audience.setVipType(Constant.VipType.SAINT);
        dataManager.saveUser(audience);
        dataManager.saveRoom(room);

        RoomManagement.RoomManagementRequest.Builder builder = RoomManagement.RoomManagementRequest.newBuilder();
        RoomManagement.ApplyConnectLiveRequest.Builder request = RoomManagement.ApplyConnectLiveRequest.newBuilder();
        var pair = applyConnectLiveRequestHandler.handle(context, builder.setApplyConnectLiveRequest(request.build()).build(), audience, room);
        Assert.assertEquals(NOT_IN_THE_ROOM,pair.getRight());
        Assert.assertEquals(0,room.getConnectLiveCodeSize());

        room.addAudience(audience);
        room.addConnectLiveCode(audience.getId(),audience.getPlatformType());
         pair = applyConnectLiveRequestHandler.handle(context, builder.setApplyConnectLiveRequest(request.build()).build(), audience, room);
        Assert.assertEquals(ALREADY_IN_THE_CONNECT_LIST,pair.getRight());
        Assert.assertEquals(1,room.getConnectLiveCodeSize());

    }

    @Test
    public void testRoomIsMuteAll() {
        liveUser.setUserId(user.getId());
        user.setLiveUserId(liveUser.getId());
        liveUser.setRoomId(room.getId());
        dataManager.saveUser(user);
        dataManager.saveLiveUser(liveUser);
        User audience =  userBuilder.createUser("test_ audience", "audience", "//audience", "audience", true);
        audience.setVipType(Constant.VipType.SAINT);
        dataManager.saveUser(audience);
        room.addAudience(audience);
        room.setMuteAll(true);
        dataManager.saveRoom(room);

        RoomManagement.RoomManagementRequest.Builder builder = RoomManagement.RoomManagementRequest.newBuilder();
        RoomManagement.ApplyConnectLiveRequest.Builder request = RoomManagement.ApplyConnectLiveRequest.newBuilder();
        var pair = applyConnectLiveRequestHandler.handle(context, builder.setApplyConnectLiveRequest(request.build()).build(), audience, room);
        Assert.assertEquals(CURRENTLY_ALL_MUTED,pair.getRight());
        Assert.assertEquals(0,room.getConnectLiveCodeSize());
    }

    @Test
    public void testAlreadyInTheConnectList() {
        liveUser.setUserId(user.getId());
        user.setLiveUserId(liveUser.getId());
        liveUser.setRoomId(room.getId());
        dataManager.saveUser(user);
        dataManager.saveLiveUser(liveUser);
        User audience =  userBuilder.createUser("test_ audience", "audience", "//audience", "audience", true);
        audience.setVipType(Constant.VipType.SAINT);
        dataManager.saveUser(audience);
        room.addAudience(audience);
        room.addConnectLiveCode(audience.getId(),audience.getPlatformType());
        dataManager.saveRoom(room);

        RoomManagement.RoomManagementRequest.Builder builder = RoomManagement.RoomManagementRequest.newBuilder();
        RoomManagement.ApplyConnectLiveRequest.Builder request = RoomManagement.ApplyConnectLiveRequest.newBuilder();
        var pair = applyConnectLiveRequestHandler.handle(context, builder.setApplyConnectLiveRequest(request.build()).build(), audience, room);
        Assert.assertEquals(ALREADY_IN_THE_CONNECT_LIST,pair.getRight());
        Assert.assertEquals(1,room.getConnectLiveCodeSize());
    }

    @Test
    public void testRoomManagerApplyConnectLiveAndTop() {
        liveUser.setUserId(user.getId());
        user.setLiveUserId(liveUser.getId());
        liveUser.setRoomId(room.getId());
        dataManager.saveUser(user);
        dataManager.saveLiveUser(liveUser);
        User audience =  userBuilder.createUser("test_ audience", "audience", "//audience", "audience", true);
        audience.setVipType(Constant.VipType.SAINT);
        dataManager.saveUser(audience);
        room.addAudience(audience);
        room.addAdministrator(audience.getId());
        room.addConnectLiveCode(ObjectId.get().toString(), Constant.PlatformType.PLATFORM_JINLI);
        room.addConnectLiveCode(ObjectId.get().toString(), Constant.PlatformType.PLATFORM_JINLI);
        room.addConnectLiveCode(ObjectId.get().toString(), Constant.PlatformType.PLATFORM_JINLI);
        room.addConnectLiveCode(ObjectId.get().toString(), Constant.PlatformType.PLATFORM_JINLI);
        dataManager.saveRoom(room);
        var code = room.getConnectLiveCode();
        Assert.assertNotEquals(code.get(0).getLeft(),audience.getId());

        RoomManagement.RoomManagementRequest.Builder builder = RoomManagement.RoomManagementRequest.newBuilder();
        RoomManagement.ApplyConnectLiveRequest.Builder request = RoomManagement.ApplyConnectLiveRequest.newBuilder();
        var pair = applyConnectLiveRequestHandler.handle(context, builder.setApplyConnectLiveRequest(request.build()).build(), audience, room);
        Assert.assertEquals(SUCCESS,pair.getRight());
        Assert.assertEquals(code.get(0).getLeft(),audience.getId());

    }
}
