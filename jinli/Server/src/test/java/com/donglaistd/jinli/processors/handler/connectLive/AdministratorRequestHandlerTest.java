package com.donglaistd.jinli.processors.handler.connectLive;

import com.donglaistd.jinli.BaseTest;
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
public class AdministratorRequestHandlerTest extends BaseTest {
    @Autowired
    private DataManager dataManager;
    @Autowired
    private AddAdministratorRequestHandler addAdministratorRequestHandler;
    @Autowired
    private RevokeAdministratorRequestHandler revokeAdministratorRequestHandler;
    @Test
    public void testAddAdministrator_FAIL() {
        liveUser.setUserId(user.getId());
        user.setLiveUserId(liveUser.getId());
        liveUser.setRoomId(room.getId());

        User audience = userBuilder.createUser("test_ audience", "audience", "audience");
        dataManager.saveUser(user);
        dataManager.saveLiveUser(liveUser);

        room.addAudience(user);
        dataManager.saveRoom(room);
        RoomManagement.RoomManagementRequest.Builder builder = RoomManagement.RoomManagementRequest.newBuilder();
        RoomManagement.AddAdministratorRequest.Builder request = RoomManagement.AddAdministratorRequest.newBuilder().setUserId(audience.getId());
        var pair = addAdministratorRequestHandler.handle(context, builder.setAddAdministratorRequest(request.build()).build(), user, room);
        Assert.assertEquals(NOT_IN_THE_ROOM,pair.getRight());
        Assert.assertEquals(0,room.getAdministrators().size());

    }

    @Test
    public void testAddAdministratorSuccess() {
        liveUser.setUserId(user.getId());
        user.setLiveUserId(liveUser.getId());
        liveUser.setRoomId(room.getId());

        User audience = userBuilder.createUser("test_ audience", "audience", "audience");
        dataManager.saveUser(user);
        dataManager.saveLiveUser(liveUser);

        room.addAudience(audience);
        dataManager.saveRoom(room);
        RoomManagement.RoomManagementRequest.Builder builder = RoomManagement.RoomManagementRequest.newBuilder();
        RoomManagement.AddAdministratorRequest.Builder request = RoomManagement.AddAdministratorRequest.newBuilder().setUserId(audience.getId());
        var pair = addAdministratorRequestHandler.handle(context, builder.setAddAdministratorRequest(request.build()).build(), user, room);
        Assert.assertEquals(SUCCESS,pair.getRight());
        Assert.assertEquals(1,room.getAdministrators().size());
    }

    @Test
    public void testAddAdministratorExceedTheLimit() {
        liveUser.setUserId(user.getId());
        user.setLiveUserId(liveUser.getId());
        liveUser.setRoomId(room.getId());

        User audience = userBuilder.createUser("test_ audience", "audience", "audience");
        dataManager.saveUser(user);
        dataManager.saveLiveUser(liveUser);
        room.addAudience(audience);
        room.addAdministrator(ObjectId.get().toString());
        room.addAdministrator(ObjectId.get().toString());
        room.addAdministrator(ObjectId.get().toString());

        dataManager.saveRoom(room);
        RoomManagement.RoomManagementRequest.Builder builder = RoomManagement.RoomManagementRequest.newBuilder();
        RoomManagement.AddAdministratorRequest.Builder request = RoomManagement.AddAdministratorRequest.newBuilder().setUserId(audience.getId());
        var pair = addAdministratorRequestHandler.handle(context, builder.setAddAdministratorRequest(request.build()).build(), user, room);
        Assert.assertEquals(THE_NUMBER_OF_ADMINISTRATORS_EXCEEDS_THE_LIMIT,pair.getRight());
        Assert.assertEquals(3,room.getAdministrators().size());
    }


    @Test
    public void testRevokeAdministratorFail() {
        liveUser.setUserId(user.getId());
        user.setLiveUserId(liveUser.getId());
        liveUser.setRoomId(room.getId());

        User audience = userBuilder.createUser("test_ audience", "audience", "audience");

        dataManager.saveUser(user);
        dataManager.saveLiveUser(liveUser);
        room.addAudience(audience);
        dataManager.saveRoom(room);
        RoomManagement.RoomManagementRequest.Builder builder = RoomManagement.RoomManagementRequest.newBuilder();
        RoomManagement.RevokeAdministratorRequest.Builder request = RoomManagement.RevokeAdministratorRequest.newBuilder().setUserId(audience.getId());
        var pair = revokeAdministratorRequestHandler.handle(context, builder.setRevokeAdministratorRequest(request.build()).build(), user, room);
        Assert.assertEquals(NOT_AN_ADMINISTRATOR, pair.getRight());
        Assert.assertEquals(0,room.getAdministrators().size());
    }

    @Test
    public void testRevokeAdministratorSuccess() {
        liveUser.setUserId(user.getId());
        user.setLiveUserId(liveUser.getId());
        liveUser.setRoomId(room.getId());

        User audience = userBuilder.createUser("test_ audience", "audience", "audience");
        dataManager.saveUser(user);
        dataManager.saveLiveUser(liveUser);
        room.addAudience(audience);
        room.addAdministrator(audience.getId());
        room.addAdministrator(ObjectId.get().toString());
        Assert.assertEquals(2, room.getAdministrators().size());

        dataManager.saveRoom(room);
        RoomManagement.RoomManagementRequest.Builder builder = RoomManagement.RoomManagementRequest.newBuilder();
        RoomManagement.RevokeAdministratorRequest.Builder request = RoomManagement.RevokeAdministratorRequest.newBuilder().setUserId(audience.getId());
        var pair = revokeAdministratorRequestHandler.handle(context, builder.setRevokeAdministratorRequest(request.build()).build(), user, room);
        Assert.assertEquals(SUCCESS, pair.getRight());
        Assert.assertEquals(1,room.getAdministrators().size());
    }

    @Test
    public void testRevokeAdministratorNotInTheRoom() {
        liveUser.setUserId(user.getId());
        user.setLiveUserId(liveUser.getId());
        liveUser.setRoomId(room.getId());

        User audience = userBuilder.createUser("test_ audience", "audience", "audience");
        dataManager.saveUser(user);
        dataManager.saveLiveUser(liveUser);
        Assert.assertEquals(0, room.getAdministrators().size());
        dataManager.saveRoom(room);
        RoomManagement.RoomManagementRequest.Builder builder = RoomManagement.RoomManagementRequest.newBuilder();
        RoomManagement.RevokeAdministratorRequest.Builder request = RoomManagement.RevokeAdministratorRequest.newBuilder().setUserId(audience.getId());
        var pair = revokeAdministratorRequestHandler.handle(context, builder.setRevokeAdministratorRequest(request.build()).build(), user, room);
        Assert.assertEquals(NOT_AN_ADMINISTRATOR, pair.getRight());
        Assert.assertEquals(0,room.getAdministrators().size());
    }

    @Test
    public void testRevokeAdministratorInsufficientPermissions() {
        liveUser.setUserId(user.getId());
        user.setLiveUserId(liveUser.getId());
        liveUser.setRoomId(room.getId());

        User audience = userBuilder.createUser("test_ audience", "audience", "audience");
        dataManager.saveUser(user);
        dataManager.saveLiveUser(liveUser);
        room.addAudience( audience);
        room.addAdministrator(audience.getId());
        room.addAdministrator(ObjectId.get().toString());
        Assert.assertEquals(2, room.getAdministrators().size());

        dataManager.saveRoom(room);
        RoomManagement.RoomManagementRequest.Builder builder = RoomManagement.RoomManagementRequest.newBuilder();
        RoomManagement.RevokeAdministratorRequest.Builder request = RoomManagement.RevokeAdministratorRequest.newBuilder().setUserId(audience.getId());
        var pair = revokeAdministratorRequestHandler.handle(context, builder.setRevokeAdministratorRequest(request.build()).build(), audience, room);
        Assert.assertEquals(INSUFFICIENT_PERMISSIONS, pair.getRight());
        Assert.assertEquals(2, room.getAdministrators().size());
    }

}
