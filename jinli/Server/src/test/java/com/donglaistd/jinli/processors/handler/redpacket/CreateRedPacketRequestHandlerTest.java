package com.donglaistd.jinli.processors.handler.redpacket;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.builder.RedPacketBuilder;
import com.donglaistd.jinli.database.dao.UserDaoService;
import org.springframework.beans.factory.annotation.Autowired;

public class CreateRedPacketRequestHandlerTest extends BaseTest {
    @Autowired
    CreateRedPacketRequestHandler createRedPacketRequestHandler;
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    RedPacketBuilder redPacketBuilder;

   /* @Test
    public void createRedPacketTest() throws InterruptedException {
        EventPublisher.isEnabled = new AtomicBoolean(true);
        int coinAmount = 1000;
        int redPacketNum = 5;
        int userCoin = 100000;
        User user = createTester(userCoin, "zsg");
        //1.create 2 redPacket
        for (int i = 0; i < 2; i++) {
            var redPacket = redPacketBuilder.create(user.getId(), coinAmount, redPacketNum);
            EventPublisher.publish(new ModifyCoinEvent(user, -coinAmount));
            redPacket.setExpireTime(1000);
            redPacket.setCountDownTime(1000);
            room.addRedPacket(redPacket);
            dataManager.saveRoom(room);
        }
        Assert.assertEquals(2, room.getRedPacketQueue().size());
        RedPacket redPacket1 = room.getFirstRedPacketQueue();
        RedPacket redPacket2 = room.getRedPacketQueue().get(1);
        Assert.assertTrue(redPacket1.getOpenTime() > 0);
        Assert.assertTrue(redPacket2.getOpenTime() < 0);

        Thread.sleep(3000);
        user = userDaoService.findById(user.getId());
        Assert.assertEquals(99000, user.getGameCoin());
        Assert.assertEquals(1, room.getRedPacketQueue().size());
        Assert.assertTrue(redPacket2.getOpenTime() > 0);
    }*/
}
