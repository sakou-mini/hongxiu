package com.donglaistd.jinli.processors.handler;


import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.builder.GameBuilder;
import com.donglaistd.jinli.config.GameInit;
import com.donglaistd.jinli.database.dao.FollowListDaoService;
import com.donglaistd.jinli.database.dao.LiveUserDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.FollowList;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.game.*;
import com.donglaistd.jinli.exception.JinliException;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.Utils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Date;

import static com.donglaistd.jinli.Constant.GameType.*;
import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RecommendRequestHandlerTest extends BaseTest {

    @Autowired
    protected DataManager dataManager;
    @Autowired
    RecommendRequestHandler recommendRequestHandler;
    @Autowired
    GameInit gameInit;
    @Autowired
    GameBuilder gameBuilder;
    @Autowired
    private UserDaoService userDaoService;
    @Autowired
    private LiveUserDaoService liveUserDaoService;
    @Autowired
    private FollowListDaoService followListDaoService;
    @Value("${longhu.betting.time}")
    private long LONGHU_BETTING_TIME;
    @Value("${baccarat.betting.time}")
    private long BACCARAT_BETTING_TIME;
    @Value("${bullbull.betting.time}")
    private long BULLBULL_BETTING_TIME;
    @Value("${redblack.betting.time}")
    private long REDBLACK_BETTING_TIME;
    @Value("${goldenflower.betting.time}")
    private long GOLDENFLOWER_BETTING_TIME;


    @Test
    public void TestRecommendNoRoom() {
        DataManager.roomMap.remove(room.getId());
        var request = Jinli.JinliMessageRequest.newBuilder();
        var reply = recommendRequestHandler.handle(context, request.build());
        var recommend = reply.getRecommendReply();
        Assert.assertEquals(0, recommend.getRoomListList().size());
    }

    @Test
    public void TestRecommendDefaultRoom() {
        DataManager.roomMap.remove(room.getId());
        gameInit.init();
        var request = Jinli.JinliMessageRequest.newBuilder();
        var reply = recommendRequestHandler.handle(context, request.build());
        var recommend = reply.getRecommendReply();
        Assert.assertEquals(5, recommend.getRoomListList().size());
    }

    @Test
    public void TestRecommendFollowerHeatRoom() throws JinliException {
        DataManager.roomMap.remove(room.getId());
        gameInit.init();

        var start1 = startGame(1);
        var room1 = ((Room) start1[1]);
        room1.addAudience(user);
        room1.addAudience(createUser(123));
        room1.addAudience(createUser(124));
        room1.addAudience(createUser(125));
        room1.addAudience(createUser(126));

        var start2 = startGame(2);
        var room2 = ((Room) start2[1]);
        room2.addAudience(user);
        room2.addAudience(createUser(123));
        room2.addAudience(createUser(124));
        room2.addAudience(createUser(125));

        var start3 = startGame(3);
        var room3 = ((Room) start3[1]);
        room3.addAudience(user);
        room3.addAudience(createUser(123));
        room3.addAudience(createUser(124));

        var start4 = startGame(4);
        var room4 = ((Room) start4[1]);
        room4.addAudience( user);
        room4.addAudience(createUser(123));

        var start5 = startGame(5);
        var room5 = ((Room) start5[1]);
        room5.addAudience( user);

        followListDaoService.save(new FollowList(user, (LiveUser) start1[2]));
        followListDaoService.save(new FollowList(user, (LiveUser) start2[2]));
        followListDaoService.save(new FollowList(user, (LiveUser) start3[2]));
        followListDaoService.save(new FollowList(user, (LiveUser) start4[2]));
        followListDaoService.save(new FollowList(user, (LiveUser) start5[2]));


        var request = Jinli.JinliMessageRequest.newBuilder();
        var reply = recommendRequestHandler.handle(context, request.build());
        var recommend = reply.getRecommendReply();
        Assert.assertEquals(10, recommend.getRoomListList().size());
        Assert.assertEquals(room1.getId(), recommend.getRoomListList().get(0).getId());
        Assert.assertEquals(room2.getId(), recommend.getRoomListList().get(1).getId());
        Assert.assertEquals(room3.getId(), recommend.getRoomListList().get(2).getId());
        Assert.assertEquals(room4.getId(), recommend.getRoomListList().get(3).getId());
        Assert.assertEquals(room5.getId(), recommend.getRoomListList().get(4).getId());
    }

    @Test
    public void TestRecommendFollowerNotEnoughRoom() throws JinliException {
        DataManager.roomMap.remove(room.getId());
        gameInit.init();

        var start1 = startGame(1);
        var room1 = ((Room) start1[1]);
        room1.addAudience( user);
        room1.addAudience( createUser(123));
        room1.addAudience( createUser(124));
        room1.addAudience( createUser(125));
        room1.addAudience( createUser(126));

        followListDaoService.save(new FollowList(user, (LiveUser) start1[2]));

        var request = Jinli.JinliMessageRequest.newBuilder();
        var reply = recommendRequestHandler.handle(context, request.build());
        var recommend = reply.getRecommendReply();
        Assert.assertEquals(6, recommend.getRoomListList().size());
        Assert.assertEquals(room1.getId(), recommend.getRoomListList().get(0).getId());

    }

    @Test
    public void TestRecommendFollowerNoRoom() {
        DataManager.roomMap.remove(room.getId());
        gameInit.init();

        followListDaoService.save(new FollowList(user, createLiveUser(111)));
        followListDaoService.save(new FollowList(user, createLiveUser(222)));
        followListDaoService.save(new FollowList(user, createLiveUser(333)));
        followListDaoService.save(new FollowList(user, createLiveUser(444)));
        followListDaoService.save(new FollowList(user, createLiveUser(555)));

        var request = Jinli.JinliMessageRequest.newBuilder();
        var reply = recommendRequestHandler.handle(context, request.build());
        var recommend = reply.getRecommendReply();
        Assert.assertEquals(5, recommend.getRoomListList().size());


    }


    private Object[] startGame(int i) throws JinliException {

        Constant.GameType[] types = Arrays.stream(values()).filter(t -> t != UNRECOGNIZED && t != EMPTY && t.getNumber()<LANDLORD_GAME_VALUE).toArray(Constant.GameType[]::new);
        User user = createUser(i);

        LiveUser liveUser = createLiveUser(user);
        user.setLiveUserId(liveUser.getId());

        Room room = new Room(liveUser);
        room.setPattern(Constant.Pattern.LIVE_AUDIO);
        room.setLiveUserId(liveUser.getId());
        room.setRoomTitle("title_test_" + i);
        room.setDescription("description_test_" + i);
        room.setCreateDate(new Date());
        room.setRoomImage("///" + i);
        liveUser.setRoomId(room.getId());

        BaseGame game = gameBuilder.createGame(types[i % types.length], liveUser);
        liveUser.setPlayingGameId(game.getGameId());

        userDaoService.save(user);
        liveUserDaoService.save(liveUser);
        DataManager.addGame(game);
        dataManager.saveUser(user);
        dataManager.saveLiveUser(liveUser);
        DataManager.roomMap.put(room.getId(), room);
        startGame(game);
        return new Object[]{game, room, liveUser, user};
    }

    private LiveUser createLiveUser(int i) {
        var user = createUser(i);
        var liveUser = createLiveUser(user);
        userDaoService.save(user);
        liveUserDaoService.save(liveUser);
        dataManager.saveUser(user);
        dataManager.saveLiveUser(liveUser);
        return liveUser;
    }


    private LiveUser createLiveUser(User user) {
        return liveUserBuilder.create(user.getId(), Constant.LiveStatus.ONLINE, user.getPlatformType());
    }

    private User createUser(int i) {
        User user = userBuilder.createNoSavedUser("test_recommend_" + i, "人鱼传说_" + i, "__" + i, "token_recommend_test_" + i,
                1000000, "", true);
        return user;
    }

    private void startGame(BaseGame game) {
        if (game.getGameStatus().equals(Constant.GameStatus.PAUSED)) {
            var bm = Jinli.StartGameBroadcastMessage.newBuilder();
            if (game instanceof Longhu) {
                game.beginGameLoop(LONGHU_BETTING_TIME);
                bm.setTimeToEnd(LONGHU_BETTING_TIME);
                bm.setGameType(Constant.GameType.LONGHU);
            } else if (game instanceof Baccarat) {
                game.beginGameLoop(BACCARAT_BETTING_TIME);
                bm.setTimeToEnd(BACCARAT_BETTING_TIME);
                bm.setGameType(Constant.GameType.BACCARAT);
            } else if (game instanceof BullBull) {
                game.beginGameLoop(BULLBULL_BETTING_TIME);
                bm.setTimeToEnd(BULLBULL_BETTING_TIME);
                bm.setGameType(Constant.GameType.NIUNIU);
            } else if (game instanceof RedBlack) {
                game.beginGameLoop(REDBLACK_BETTING_TIME);
                bm.setTimeToEnd(REDBLACK_BETTING_TIME);
                bm.setGameType(Constant.GameType.REDBLACK);
            } else if (game instanceof GoldenFlower) {
                game.beginGameLoop(GOLDENFLOWER_BETTING_TIME);
                bm.setTimeToEnd(GOLDENFLOWER_BETTING_TIME);
                bm.setGameType(Constant.GameType.GOLDENFLOWER);
            }
            var room = DataManager.roomMap.get(game.getOwner().getRoomId());
            bm.setRoomId(room.getId());
            bm.setLeftCardCount(game.getDeckLeftCount());
            bm.setDealtCardCount(game.getDeckDealtCount());
            room.broadCastToAllPlatform(buildReply(bm, SUCCESS));
        }
    }
}
