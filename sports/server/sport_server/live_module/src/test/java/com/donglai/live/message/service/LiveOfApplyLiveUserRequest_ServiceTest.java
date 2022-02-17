package com.donglai.live.message.service;

import com.donglai.model.db.entity.common.User;
import com.donglai.live.BaseTest;
import com.donglai.model.db.entity.live.LiveUser;
import com.donglai.model.db.entity.live.Room;
import com.donglai.model.db.service.live.LiveUserService;
import com.donglai.model.db.service.live.RoomService;
import com.donglai.model.db.service.common.UserService;
import com.donglai.live.entityBuilder.UserBuilder;
import com.donglai.live.message.services.LiveOfApplyLiveUserRequest_Service;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.Live;
import com.donglai.protocol.message.KafkaMessage;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.donglai.protocol.Constant.ResultCode.SUCCESS;
import static com.donglai.protocol.Constant.ResultCode.UNOFFICIAL_USER;

public class LiveOfApplyLiveUserRequest_ServiceTest extends BaseTest {
    @Autowired
    LiveOfApplyLiveUserRequest_Service applyLiveUserRequest_service;
    @Autowired
    UserBuilder userBuilder;
    @Autowired
    UserService userService;
    @Autowired
    LiveUserService liveUserService;
    @Autowired
    RoomService roomService;

    @Test
    public void TestApplyLiveUser(){
        User testUser = createUser();
        Live.LiveOfApplyLiveUserRequest applyLiveUserRequest = Live.LiveOfApplyLiveUserRequest.newBuilder().setRealName("realName")
                .setGender(Constant.GenderType.FEMALE).setCountry("country")
                .setAddress("address")
                .setEmail("email").setContactWay("contactWay").setBirthDay(System.currentTimeMillis() + "").setBankName("nh")
                .setBankCard("1235353").addImages("/test").build();
        HongXiu.HongXiuMessageRequest hongXiuMessageRequest = buildMessageRequest(applyLiveUserRequest);
        KafkaMessage.TopicMessage message = applyLiveUserRequest_service.Process(testUser.getId(), hongXiuMessageRequest);
        Assert.assertEquals(UNOFFICIAL_USER, message.getResultCode());

        testUser.setTourist(false);
        userService.save(testUser);
        message = applyLiveUserRequest_service.Process(testUser.getId(), hongXiuMessageRequest);
        Assert.assertEquals(SUCCESS, message.getResultCode());
        testUser = userService.findById(testUser.getId());
        LiveUser testLiveUser = liveUserService.findById(testUser.getLiveUserId());
        Assert.assertNotNull(testLiveUser);
        Room testRoom = roomService.findById(testLiveUser.getRoomId());
        Assert.assertNotNull(testRoom);
    }


    public User createUser(){
        User user = userBuilder.createUser("pwd", "1","avatar",0, Constant.PlatformType.SPORT);
        return userService.save(user);
    }
}
