package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.builder.GameBuilder;
import com.donglaistd.jinli.database.dao.RoomDaoService;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.game.BankerGame;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.util.DataManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.donglaistd.jinli.processors.handler.MessageHandler.ROOM_KEY;
import static com.donglaistd.jinli.processors.handler.MessageHandler.USER_KEY;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QuitRoomRequestHandlerTest extends BaseTest {
    @Autowired
    private QuitRoomRequestHandler deleteAudienceRequestHandler;
    @Autowired
    RoomDaoService roomDaoService;

    @Test
    public void testQuitRoom_DoesNotExist() {
        Jinli.JinliMessageRequest.Builder builder = Jinli.JinliMessageRequest.newBuilder();
        builder.setQuitRoomRequest(Jinli.QuitRoomRequest.newBuilder().setRoomId("1").build());
        Jinli.JinliMessageReply handle = deleteAudienceRequestHandler.handle(context, builder.build() );
        Assert.assertEquals(Constant.ResultCode.ROOM_DOES_NOT_EXIST, handle.getResultCode());
    }

    @Test
    public void testQuitRoomSuccess() {
        Room room = DataManager.roomMap.get(this.room.getId());
        Jinli.JinliMessageRequest.Builder builder = Jinli.JinliMessageRequest.newBuilder();
        builder.setQuitRoomRequest(Jinli.QuitRoomRequest.newBuilder().setRoomId(room.getId()).build());
        Jinli.JinliMessageReply handle = deleteAudienceRequestHandler.handle(context, builder.build() );
        Assert.assertEquals(Constant.ResultCode.SUCCESS, handle.getResultCode());
    }

    @Test
    public void testQuitRoomSuccess_RemoveAudience() {
        Room room = DataManager.roomMap.get(this.room.getId());
        room.addAudience(user);
        Assert.assertEquals(1, room.getAllPlatformAudienceList().size());
        Jinli.JinliMessageRequest.Builder builder = Jinli.JinliMessageRequest.newBuilder();
        builder.setQuitRoomRequest(Jinli.QuitRoomRequest.newBuilder().setRoomId(room.getId()).build());
        Jinli.JinliMessageReply handle = deleteAudienceRequestHandler.handle(context, builder.build() );
        Assert.assertEquals(Constant.ResultCode.SUCCESS, handle.getResultCode());
        Assert.assertEquals(0, room.getAllPlatformAudienceList().size());
    }

    @Override
    public void setUp() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        super.setUp();
        EventPublisher.isEnabled = new AtomicBoolean(true);
    }

    // when user become Banker
    @Autowired
    private GameBuilder gameBuilder;
    @Test
    public void becomeBankerAndQuiteTest() throws InterruptedException {
        //room.addAudience(channel, user);
        BankerGame game = (BankerGame) gameBuilder.createGame(Constant.GameType.NIUNIU, liveUser, true);
        User bankerUser1 = createTester(90000, "bankerUser1");
        bankerUser1.setOnline(true);
        bankerUser1.setCurrentRoomId(room.getId());
        dataManager.saveUser(bankerUser1);
        game.addWaitingBanker(bankerUser1,10000);
        game.setDelayFinishTime(1000);
        game.beginGameLoop(3000);
        DataManager.addGame(game);
        room.addAudience(bankerUser1);
        Assert.assertEquals(game.getBanker(),bankerUser1);
        Assert.assertFalse(room.notContainsUser(bankerUser1));

        Thread.sleep(2000);
        Jinli.JinliMessageRequest.Builder builder = Jinli.JinliMessageRequest.newBuilder();
        builder.setQuitRoomRequest(Jinli.QuitRoomRequest.newBuilder().setRoomId(room.getId()).build());
        Jinli.JinliMessageReply reply = deleteAudienceRequestHandler.doHandle(context, builder.build(), bankerUser1);
        Assert.assertEquals(reply.getResultCode(), Constant.ResultCode.SUCCESS);

        Thread.sleep(2500);
        Assert.assertEquals(game.getBanker(),null);
        Assert.assertTrue(room.notContainsUser(bankerUser1));
        Assert.assertEquals("",bankerUser1.getCurrentRoomId());
    }

    public Channel mockUserChannel(User user){
        ChannelHandlerContext userContext = Mockito.mock(ChannelHandlerContext.class);
        Channel userChannel = Mockito.mock(Channel.class);
        Mockito.when(userChannel.attr(USER_KEY)).thenReturn(attributeUser);
        Mockito.when(userChannel.attr(ROOM_KEY)).thenReturn(attributeRoom);
        Mockito.when(attributeUser.get()).thenReturn(user.getId());
        Mockito.when(userContext.channel()).thenReturn(userChannel);
        return userChannel;
    }

}
