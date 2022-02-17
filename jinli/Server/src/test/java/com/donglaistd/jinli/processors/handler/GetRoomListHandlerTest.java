package com.donglaistd.jinli.processors.handler;


import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.builder.BullBullBuilder;
import com.donglaistd.jinli.builder.LiveUserBuilder;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.game.BullBull;
import com.donglaistd.jinli.util.DataManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;


@RunWith(SpringRunner.class)
@SpringBootTest
public class GetRoomListHandlerTest extends BaseTest {
    @Autowired
    private GetRoomListRequestHandler getRoomListHandler;
    @Autowired
    private BullBullBuilder bullBullBuilder;
    @Autowired
    LiveUserBuilder liveUserBuilder;

    @Test
    public void testGetRoomList_GameIsEmpty() {
        Room room = DataManager.roomMap.get(this.room.getId());
       // LiveUser liveUser = liveUserBuilder.createLiveUser();
        liveUser.setUserId(user.getId());
        liveUser.setLiveUrl("//test1");
        liveUser.setRoomId(room.getId());
        liveUser.setLiveStatus(Constant.LiveStatus.ONLINE);
        dataManager.saveLiveUser(liveUser);
        room.setLiveUserId(liveUser.getId());
        Jinli.JinliMessageRequest.Builder request = Jinli.JinliMessageRequest.newBuilder();
        Jinli.GetRoomListRequest.Builder builder = Jinli.GetRoomListRequest.newBuilder();
        builder.setMaxCount(5);
        Jinli.JinliMessageReply reply = getRoomListHandler.handle(context, request.setGetRoomListRequest(builder.build()).build());
        Assert.assertEquals(0, reply.getGetRoomListReply().getRoomListList().size());

    }


    @Test
    public void testGetRoomList_GameNotEmpty() {
        BullBull bullBull = bullBullBuilder.create(true);
        liveUser.setLiveStatus(Constant.LiveStatus.ONLINE);
        bullBull.setOwner(liveUser);
        bullBull.setBettingTime(29000);
        liveUser.setPlayingGameId(bullBull.getGameId());
        dataManager.saveLiveUser(liveUser);
        DataManager.addGame(bullBull);

        Jinli.JinliMessageRequest.Builder request = Jinli.JinliMessageRequest.newBuilder();
        Jinli.GetRoomListRequest.Builder builder = Jinli.GetRoomListRequest.newBuilder();
        builder.setMaxCount(5);
        Jinli.JinliMessageReply reply = getRoomListHandler.handle(context, request.setGetRoomListRequest(builder.build()).build());
        Assert.assertEquals(1, reply.getGetRoomListReply().getRoomListList().size());
    }

    @Test
    public void testGetRoomList_Multiple() {
        BullBull bullBull1 = bullBullBuilder.create(true);
        BullBull bullBull2 = bullBullBuilder.create(true);
        Room room = DataManager.roomMap.get(this.room.getId());
        bullBull1.setOwner(liveUser);
        bullBull1.setBettingTime(29000);
        liveUser.setPlayingGameId(bullBull1.getGameId());
        room.setLiveUserId(liveUser.getId());
        room.setStartDate(new Date());
        room.initPlatformRoomData(Constant.PlatformType.PLATFORM_JINLI,user.getId(),0);
        dataManager.saveLiveUser(liveUser);

        User second_user = userBuilder.createUser("second__accountName", "second__displayName", "second__token");

        LiveUser second_liveUser = liveUserBuilder.create(second_user.getId(),Constant.LiveStatus.ONLINE, Constant.PlatformType.PLATFORM_JINLI);
        second_liveUser.setLiveStatus(Constant.LiveStatus.ONLINE);
        second_liveUser.setLiveUrl("///test3");

        Room second_room = roomBuilder.create(second_liveUser.getId(), second_user.getId(), "second_room_title", "second room description", "");
        second_room.initPlatformRoomData(Constant.PlatformType.PLATFORM_JINLI,second_user.getId(),0);
        second_room.setStartDate(new Date());

        second_liveUser.setRoomId(second_room.getId());

        bullBull2.setBettingTime(29000);
        second_liveUser.setPlayingGameId(bullBull2.getGameId());
        second_liveUser.setUserId(second_user.getId());
        bullBull1.setOwner(liveUser);
        dataManager.saveLiveUser(second_liveUser);
        dataManager.saveUser(second_user);
        dataManager.saveRoom(second_room);

        DataManager.addGame(bullBull1);
        DataManager.addGame(bullBull2);
        Jinli.JinliMessageRequest.Builder request = Jinli.JinliMessageRequest.newBuilder();
        Jinli.GetRoomListRequest.Builder builder = Jinli.GetRoomListRequest.newBuilder();
        builder.setMaxCount(5);
        Jinli.JinliMessageReply reply = getRoomListHandler.handle(context, request.setGetRoomListRequest(builder.build()).build());
        Assert.assertEquals(2, reply.getGetRoomListReply().getRoomListList().size());
    }

    @Test
    public void testGetRoomList_MaxCount() {
        int maxCount = 1;
        BullBull bullBull1 = bullBullBuilder.create(true);
        BullBull bullBull2 = bullBullBuilder.create(true);
        liveUser.setUserId(user.getId());
        liveUser.setLiveUrl("//test2");
        liveUser.setRoomId(room.getId());
        liveUser.setLiveStatus(Constant.LiveStatus.ONLINE);
        bullBull1.setOwner(liveUser);
        bullBull1.setBettingTime(29000);
        liveUser.setPlayingGameId(bullBull1.getGameId());
        room.setLiveUserId(liveUser.getId());
        room.initPlatformRoomData(Constant.PlatformType.PLATFORM_JINLI,user.getId(),0);


        User second_user = userBuilder.createUser("second__accountName", "second__displayName", "second__token");
        LiveUser second_liveUser = liveUserBuilder.create(second_user.getId(),Constant.LiveStatus.ONLINE, Constant.PlatformType.PLATFORM_JINLI);
        second_liveUser.setLiveUrl("///test3");

        Room second_room = roomBuilder.create(second_liveUser.getId(), second_user.getId(), "second_room_title", "second room description", "");
        second_room.initPlatformRoomData(Constant.PlatformType.PLATFORM_JINLI,second_user.getId(),0);
        second_room.setStartDate(new Date());

        second_liveUser.setRoomId(second_room.getId());
        second_liveUser.setPlayingGameId(bullBull2.getGameId());

        bullBull2.setBettingTime(29000);
        bullBull1.setOwner(liveUser);
        DataManager.roomMap.put(second_room.getId(), second_room);
        DataManager.addGame(bullBull1);
        DataManager.addGame(bullBull2);
        dataManager.saveLiveUser(liveUser);
        dataManager.saveLiveUser(second_liveUser);
        dataManager.saveUser(second_user);
        Jinli.JinliMessageRequest.Builder request = Jinli.JinliMessageRequest.newBuilder();
        Jinli.GetRoomListRequest.Builder builder = Jinli.GetRoomListRequest.newBuilder();
        builder.setMaxCount(maxCount);
        Jinli.JinliMessageReply reply = getRoomListHandler.handle(context, request.setGetRoomListRequest(builder.build()).build());
        Assert.assertEquals(1, reply.getGetRoomListReply().getRoomListList().size());
    }
}
