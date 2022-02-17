package com.donglaistd.jinli.processors.handler.gift;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.service.EventPublisher;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;
import static com.donglaistd.jinli.processors.handler.MessageHandler.ROOM_KEY;
import static com.donglaistd.jinli.processors.handler.MessageHandler.USER_KEY;

public class SendGiftRequestHandlerTest extends BaseTest {
    @Autowired
    private SendGiftRequestHandler sendGiftRequestHandler;
    @Autowired
    UserDaoService userDaoService;

    private User receiver;

    @Before
    @Override
    public void setUp() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        super.setUp();
        receiver = createTester(0, "giftReceiver");
        receiver.setCurrentRoomId(room.getId());
        dataManager.saveUser(receiver);
        EventPublisher.isEnabled = new AtomicBoolean(true);
    }

    @Test
    public void TestSendGiftToPlayerSuccessful() throws InterruptedException {
        User sender = createTester(10000, "receiver");
        /*user.setGameCoin(10000);
        dataManager.saveUser(user);*/
        var sendGiftMessage = Jinli.JinliMessageRequest.newBuilder().setSendGiftRequest(Jinli.SendGiftRequest.newBuilder()
                .setGiftId("10008").setSendAmount(10).setReceiveUserId(this.receiver.getId())).build();
        var reply = sendGiftRequestHandler.doHandle(context, sendGiftMessage, sender);
        this.receiver = userDaoService.findById(this.receiver.getId());
        Assert.assertEquals(SUCCESS, reply.getResultCode());
        Thread.sleep(1000);
        sender = userDaoService.findById(sender.getId());
        Assert.assertEquals(500, this.receiver.getGameCoin());
        Assert.assertEquals(9000, sender.getGameCoin());
    }

    @Test
    public void TestBuyGiftNotEnoughCoin() {
        user.setGameCoin(9999);
        dataManager.saveUser(user);
        var message = Jinli.JinliMessageRequest.newBuilder().setSendGiftRequest(Jinli.SendGiftRequest.newBuilder().setGiftId("10000").setSendAmount(10000)
                .setReceiveUserId(receiver.getId()));
        var reply = sendGiftRequestHandler.handle(context, message.build());
        user = userDaoService.findById(user.getId());
        Assert.assertEquals(9999, user.getGameCoin());
        Assert.assertEquals(Constant.ResultCode.NOT_ENOUGH_GAMECOIN, reply.getResultCode());
    }

    @Test
    public void TestBuyGiftNotExist() {
        user.setGameCoin(9999);
        dataManager.saveUser(user);
        var message = Jinli.JinliMessageRequest.newBuilder().setSendGiftRequest(Jinli.SendGiftRequest.newBuilder().setGiftId("gift_not_exist").setSendAmount(1)
                .setReceiveUserId(receiver.getId()));
        var reply = sendGiftRequestHandler.handle(context, message.build());
        Assert.assertEquals(9999, user.getGameCoin());
        Assert.assertEquals(Constant.ResultCode.GIFT_NOT_EXIST, reply.getResultCode());
    }

    @Test
    public void TestSendGiftConcurrently() throws InterruptedException {
        User sender = createTester(999, "sender");
        User receiver = createTester(888, "receiver");
        var sendGiftMessage = Jinli.JinliMessageRequest.newBuilder().setSendGiftRequest(Jinli.SendGiftRequest.newBuilder()
                .setGiftId("10008").setSendAmount(1).setReceiveUserId(receiver.getId())).build();
        var sendGiftMessage2 = Jinli.JinliMessageRequest.newBuilder().setSendGiftRequest(Jinli.SendGiftRequest.newBuilder()
                .setGiftId("10008").setSendAmount(2).setReceiveUserId(sender.getId())).build();

        ChannelHandlerContext senderContext = getChannelHandlerContext(sender);
        ChannelHandlerContext receiverContext = getChannelHandlerContext(receiver);

        var t1 = new Thread(() -> sendGiftRequestHandler.handle(senderContext, sendGiftMessage));
        var t2 = new Thread(() -> sendGiftRequestHandler.handle(receiverContext, sendGiftMessage2));
        t1.start();
        t2.start();
        Thread.sleep(1000);
        receiver = userDaoService.findById(receiver.getId());
        sender = userDaoService.findById(sender.getId());
        Assert.assertEquals(999, sender.getGameCoin()); //999-100 + 100
        Assert.assertEquals(738, receiver.getGameCoin()); //888-200+50
    }

    private ChannelHandlerContext getChannelHandlerContext(User receiver) {
        var cleanContext = Mockito.mock(ChannelHandlerContext.class);
        var cleanChannel = Mockito.mock(Channel.class);
        var attributeUser = Mockito.mock(Attribute.class);
        var attributeRoom = Mockito.mock(Attribute.class);
        Mockito.when(cleanContext.channel()).thenReturn(cleanChannel);
        Mockito.when(cleanChannel.attr(USER_KEY)).thenReturn(attributeUser);
        Mockito.when(cleanChannel.attr(ROOM_KEY)).thenReturn(attributeRoom);
        Mockito.when(attributeUser.get()).thenReturn(receiver.getId());
        Mockito.when(cleanContext.channel()).thenReturn(cleanChannel);
        return cleanContext;
    }
}
