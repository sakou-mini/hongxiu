package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.builder.GameBuilder;
import com.donglaistd.jinli.builder.RoomBuilder;
import com.donglaistd.jinli.database.dao.FollowListDaoService;
import com.donglaistd.jinli.database.dao.LiveUserDaoService;
import com.donglaistd.jinli.database.dao.RoomDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.FollowList;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.exception.JinliException;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.TimeUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static com.donglaistd.jinli.Constant.LiveStatus.OFFLINE;
import static com.donglaistd.jinli.Constant.LiveStatus.ONLINE;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FindAllFolloweesRequestHandlerTest extends BaseTest {

    @Autowired
    private FindAllFolloweesRequestHandler handler;
    @Autowired
    private UserDaoService userDaoService;
    @Autowired
    private LiveUserDaoService liveUserDaoService;
    @Autowired
    private FollowListDaoService followListDaoService;
    @Autowired
    private RoomDaoService roomDaoService;
    @Autowired
    GameBuilder gameBuilder;
    @Autowired
    RoomBuilder roomBuilder;


    @Test

    public void TestFindOneFolloweeAndOneRecommend() throws JinliException {
        user.setAccountName("a");
        user.setDisplayName("a");
        user.setToken("a");
        userDaoService.save(user);

        var userB = createTester(0, "B");
        userDaoService.save(userB);

        var userC = createTester(0, "C");
        userDaoService.save(userC);

        var liveUser = liveUserBuilder.create(userB.getId(),ONLINE, Constant.PlatformType.PLATFORM_JINLI);
        Room roomB = roomBuilder.create(liveUser.getId(),userB.getId(),"","","");
        roomB.setPattern(Constant.Pattern.LIVE_VIDEO);
        dataManager.saveRoom(roomB);
        liveUser.setLiveStatus(Constant.LiveStatus.OFFLINE);
        liveUser.setRoomId(roomB.getId());
        liveUserDaoService.save(liveUser);
        roomB.setLiveUserId(liveUser.getId());
        roomDaoService.save(roomB);

        var liveUserC =  liveUserBuilder.create(userC.getId(),ONLINE, Constant.PlatformType.PLATFORM_JINLI);
        var roomC = roomBuilder.create(liveUser.getId(),userC.getId(),"","","");
        dataManager.saveRoom(roomC);
        roomC.setPattern(Constant.Pattern.LIVE_VIDEO);
        liveUserC.setRoomId(roomC.getId());

        var game = gameBuilder.createGame(Constant.GameType.LONGHU,liveUserC);
        DataManager.addGame(game);
        liveUserC.setPlayingGameId(game.getGameId());
        liveUserDaoService.save(liveUserC);
        roomC.setLiveUserId(liveUserC.getId());
        roomDaoService.save(roomC);
        dataManager.saveUser(userC);
        dataManager.saveLiveUser(liveUser);
        var followList = new FollowList(user, liveUserC);
        followListDaoService.save(followList);

        var requestWrapper = Jinli.JinliMessageRequest.newBuilder();
        var request = Jinli.FindAllFolloweesRequest.newBuilder();

        var message = requestWrapper.setFindAllFolloweesRequest(request).build();
        var reply = handler.handle(context, message);

        Assert.assertEquals(Constant.ResultCode.SUCCESS, handler.resultCode);
        Assert.assertEquals(1, reply.getFindAllFolloweesReply().getRoomListList().size());
        Assert.assertEquals(0, reply.getFindAllFolloweesReply().getRecommendedList().size());
    }

    @Test
    public void FindOfflineUserInfo() {
        liveUser.setLiveStatus(Constant.LiveStatus.OFFLINE);
        liveUserDaoService.save(liveUser);
        var followList = new FollowList(user, liveUser);
        var room = new Room();
        room.setLiveUserId(liveUser.getId());
        roomDaoService.save(room);
        followListDaoService.save(followList);


        var requestWrapper = Jinli.JinliMessageRequest.newBuilder();
        var request = Jinli.FindAllFolloweesRequest.newBuilder();

        var message = requestWrapper.setFindAllFolloweesRequest(request).build();
        var reply = handler.handle(context, message);

        Assert.assertEquals(Constant.ResultCode.SUCCESS, handler.resultCode);
        Assert.assertEquals(1, reply.getFindAllFolloweesReply().getLiveUserInfoCount());
    }

    @Test
    public void countLiveUserFansByTimeTest(){

        LiveUser liveUser1 =liveUserBuilder.create("1111111", ONLINE,Constant.PlatformType.PLATFORM_JINLI);
        LiveUser liveUser2 = liveUserBuilder.create("2222222", ONLINE,Constant.PlatformType.PLATFORM_JINLI);
        User userA = userBuilder.createUser("a","a","a");
        User userB = userBuilder.createUser("b","b","a");
        User userC = userBuilder.createUser("c","c","a");
        FollowList followList1 = new FollowList(userA, liveUser1);
        FollowList followList2 = new FollowList(userB, liveUser1);
        FollowList followList3 = new FollowList(userC, liveUser2);
        FollowList followList4 = new FollowList(userA, liveUser2);
        followListDaoService.save(followList1);
        followListDaoService.save(followList2);
        followListDaoService.save(followList3);
        followListDaoService.save(followList4);
        long startTime = TimeUtil.getCurrentDayStartTime();

        Map<String, Integer> fansMap = followListDaoService.countLiveUserFansNumByTimes(startTime, System.currentTimeMillis());
        Assert.assertEquals(2, fansMap.get(liveUser1.getId()).intValue());
        Assert.assertEquals(2, fansMap.get(liveUser2.getId()).intValue());
    }
}
