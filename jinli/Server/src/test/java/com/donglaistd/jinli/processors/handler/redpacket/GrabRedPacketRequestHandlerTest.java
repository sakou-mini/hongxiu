package com.donglaistd.jinli.processors.handler.redpacket;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.builder.RedPacketBuilder;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.RedPacket;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.event.ModifyCoinEvent;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.util.DataManager;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.donglaistd.jinli.Constant.ResultCode.REDPACKET_NOT_READY;
import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;

public class GrabRedPacketRequestHandlerTest extends BaseTest {
    @Autowired
    GrabRedPacketRequestHandler grabRedPacketRequestHandler;

    @Autowired
    RedPacketBuilder redPacketBuilder;

    @Autowired
    UserDaoService userDaoService;

    @Override
    public void setUp() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        super.setUp();
        EventPublisher.isEnabled = new AtomicBoolean(true);
    }
/*
    public void sendRedPacket(User user, int coinAmount, int redPacketNum, int count, long overTime, long countDownTime) {
        DataManager.saveRoomKeyToChannel(context, room.getId());
        for (int i = 0; i < count; i++) {
            var redPacket = redPacketBuilder.create(user.getId(), coinAmount, redPacketNum);
            redPacket.setExpireTime(overTime);
            redPacket.setCountDownTime(countDownTime);
            EventPublisher.publish(new ModifyCoinEvent(user, -coinAmount));
            room.addRedPacket(redPacket);
            dataManager.saveRoom(room);
        }
    }

    @Test
    public void grabRedPacketAndGiveBackRedPacketTest() throws InterruptedException {
        EventPublisher.isEnabled = new AtomicBoolean(true);
        User redPackSender = createTester(100000, "12345");
        sendRedPacket(redPackSender, 1000, 1, 2, 1000, 1000);
        Assert.assertEquals(98000, userDaoService.findById(redPackSender.getId()).getGameCoin());
        RedPacket firstRedPacket = room.getFirstRedPacketQueue();
        RedPacket lastRedPacket = room.getRedPacketQueue().get(room.getRedPacketQueue().size() - 1);

        Jinli.JinliMessageRequest.Builder builder = Jinli.JinliMessageRequest.newBuilder();
        Jinli.GrabRedPacketRequest.Builder request = Jinli.GrabRedPacketRequest.newBuilder().setRedPacketId(lastRedPacket.getId());
        Thread.sleep(500);
        builder.setGrabRedPacketRequest(request.build());
        Jinli.JinliMessageReply reply = grabRedPacketRequestHandler.doHandle(context, builder.build(), user);
        Assert.assertEquals(REDPACKET_NOT_READY, reply.getResultCode());

        Thread.sleep(1000);
        request.clear();
        request.setRedPacketId(firstRedPacket.getId());
        builder.setGrabRedPacketRequest(request);
        reply = grabRedPacketRequestHandler.doHandle(context, builder.build(), user);
        Assert.assertEquals(SUCCESS, reply.getResultCode());
        Assert.assertEquals(1000, userDaoService.findById(user.getId()).getGameCoin());
        Assert.assertEquals(1, room.getRedPacketQueue().size());
        Assert.assertEquals(lastRedPacket.getId(), room.getFirstRedPacketQueue().getId());
        Thread.sleep(2200);
        Assert.assertEquals(99000, userDaoService.findById(redPackSender.getId()).getGameCoin());
    }

    @Test
    public void grabRedPacketAndCheckRedPacketIsOverTest() throws InterruptedException {
        int coinAmount = 1000;
        int redPacketNum = 2;
        User redPackSender = createTester(10000, "zsgss");
        sendRedPacket(redPackSender, coinAmount, redPacketNum, 3, 1000, 200);
        Assert.assertEquals(3, room.getRedPacketQueue().size());
        Assert.assertEquals(7000, userDaoService.findById(redPackSender.getId()).getGameCoin());

        RedPacket redPacket1 = room.getFirstRedPacketQueue();
        Thread.sleep(220);
        Jinli.JinliMessageRequest.Builder builder = Jinli.JinliMessageRequest.newBuilder();
        Jinli.GrabRedPacketRequest.Builder request = Jinli.GrabRedPacketRequest.newBuilder().setRedPacketId(redPacket1.getId());
        builder.setGrabRedPacketRequest(request.build());
        Jinli.JinliMessageReply reply = grabRedPacketRequestHandler.doHandle(context, builder.build(), user);
        Assert.assertEquals(SUCCESS, reply.getResultCode());
        Assert.assertEquals(1, redPacket1.getLeftRedPacketNum().get());
        Assert.assertTrue(redPacket1.getOpenTime() > 0);
        Assert.assertTrue(room.getRedPacketQueue().get(room.getRedPacketQueue().size() - 1).getOpenTime() < 0);
    }*/
}
