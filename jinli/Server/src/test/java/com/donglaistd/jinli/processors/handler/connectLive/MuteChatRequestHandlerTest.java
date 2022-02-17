package com.donglaistd.jinli.processors.handler.connectLive;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.RoomManagement;
import com.donglaistd.jinli.database.dao.BlacklistDaoService;
import com.donglaistd.jinli.database.entity.Blacklist;
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
public class MuteChatRequestHandlerTest extends BaseTest {
    @Autowired
    private DataManager dataManager;
    @Autowired
    private MuteChatRequestHandler muteChatRequestHandler;
    @Autowired
    private UnMuteChatRequestHandler unMuteChatRequestHandler;
    @Autowired
    private BlacklistDaoService blacklistDaoService;

    @Test
    public void testMuteChatSuccess() {
        liveUser.setUserId(user.getId());
        user.setLiveUserId(liveUser.getId());
        liveUser.setRoomId(room.getId());

        User audience =  userBuilder.createUser("test_ audience", "audience", "audience");
        dataManager.saveUser(user);
        dataManager.saveLiveUser(liveUser);

        room.addAudience(user);
        dataManager.saveRoom(room);
        RoomManagement.RoomManagementRequest.Builder builder = RoomManagement.RoomManagementRequest.newBuilder();
        RoomManagement.MuteChatRequest.Builder request = RoomManagement.MuteChatRequest.newBuilder().setUserId(audience.getId());
        var pair = muteChatRequestHandler.handle(context, builder.setMuteChatRequest(request.build()).build(), user, room);
        Assert.assertEquals(SUCCESS,pair.getRight());

        Blacklist blacklist = blacklistDaoService.findByRoomId(room.getId());
        Assert.assertNotNull(blacklist);
        boolean muteChat = blacklist.containsMuteChat(audience.getId());
        Assert.assertTrue(muteChat);
        dataManager.removeUser(audience);
    }


    @Test
    public void testMuteChatFail() {
        liveUser.setUserId(user.getId());
        user.setLiveUserId(liveUser.getId());
        liveUser.setRoomId(room.getId());

        User audience = userBuilder.createNoSavedUser("test_ audience", "audience", "audience");
//        dataManager.saveUser(audience);
        dataManager.saveUser(user);
        dataManager.saveLiveUser(liveUser);

        room.addAudience(user);
        dataManager.saveRoom(room);
        RoomManagement.RoomManagementRequest.Builder builder = RoomManagement.RoomManagementRequest.newBuilder();
        RoomManagement.MuteChatRequest.Builder request = RoomManagement.MuteChatRequest.newBuilder().setUserId(audience.getId());
        var pair = muteChatRequestHandler.handle(context, builder.setMuteChatRequest(request.build()).build(), user, room);
        Assert.assertEquals(USER_NOT_FOUND,pair.getRight());
    }

    @Test
    public void testUnMuteChatSuccess() {
        liveUser.setUserId(user.getId());
        user.setLiveUserId(liveUser.getId());
        liveUser.setRoomId(room.getId());

        User audience = userBuilder.createUser("test_ audience", "audience", "audience");
        dataManager.saveUser(user);
        dataManager.saveLiveUser(liveUser);

        room.addAudience(user);
        dataManager.saveRoom(room);
        RoomManagement.RoomManagementRequest.Builder builder = RoomManagement.RoomManagementRequest.newBuilder();
        RoomManagement.MuteChatRequest.Builder request = RoomManagement.MuteChatRequest.newBuilder().setUserId(audience.getId());
        var pair = muteChatRequestHandler.handle(context, builder.setMuteChatRequest(request.build()).build(), user, room);
        Assert.assertEquals(SUCCESS,pair.getRight());

        Blacklist blacklist = blacklistDaoService.findByRoomId(room.getId());
        Assert.assertNotNull(blacklist);
        boolean muteChat = blacklist.containsMuteChat(audience.getId());
        Assert.assertTrue(muteChat);


        RoomManagement.UnMuteChatRequest.Builder unmuteChatRequest = RoomManagement.UnMuteChatRequest.newBuilder().setUserId(audience.getId());
        pair = unMuteChatRequestHandler.handle(context, builder.setUnMuteChatRequest(unmuteChatRequest.build()).build(), audience, room);
        Assert.assertEquals(INSUFFICIENT_PERMISSIONS, pair.getRight());

        pair = unMuteChatRequestHandler.handle(context, builder.setUnMuteChatRequest(unmuteChatRequest.build()).build(), user, room);
        Assert.assertEquals(SUCCESS, pair.getRight());
         blacklist = blacklistDaoService.findByRoomId(room.getId());
        Assert.assertNotNull(blacklist);
         muteChat = blacklist.containsMuteChat(audience.getId());
        Assert.assertFalse(muteChat);

    }

}
