package com.donglai.live.message.service;

import com.donglai.model.db.entity.common.User;
import com.donglai.live.BaseTest;
import com.donglai.model.db.service.common.UserService;
import com.donglai.live.entityBuilder.UserBuilder;
import com.donglai.live.message.services.gift.LiveOfSendGiftRequest_Service;
import com.donglai.protocol.Constant;
import com.donglai.protocol.Live;
import com.donglai.protocol.message.KafkaMessage;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class LiveOfSendGiftRequestTest extends BaseTest {
    @Autowired
    UserBuilder userBuilder;
    @Autowired
    LiveOfSendGiftRequest_Service sendGiftRequest_service;
    @Autowired
    UserService userService;
    @Test
    public void test() throws InterruptedException {
        User sender = createUser("account_sender", "123456", 100000);
        var request = Live.LiveOfSendGiftRequest.newBuilder().setGiftId("10008").setGiftNum(10).setReceiveUserId(user.getId());
        KafkaMessage.TopicMessage result = sendGiftRequest_service.Process(sender.getId(), buildMessageRequest(request.build()));
        Assert.assertEquals(Constant.ResultCode.SUCCESS,result.getResultCode());
        sender = userService.findById(sender.getId());
        Assert.assertEquals(99990, sender.getCoinNumber());

        result = sendGiftRequest_service.Process(sender.getId(), buildMessageRequest(request.build()));
        sender = userService.findById(sender.getId());
        Assert.assertEquals(99980, sender.getCoinNumber());
    }

    User createUser(String accountId,String pwd,int coin){
        User user = userBuilder.createUser(accountId, pwd, "mobileCode","avatar",0,Constant.PlatformType.DUOCAI);
        user.addCoin(coin);
        userService.save(user);
        return user;
    }
}
