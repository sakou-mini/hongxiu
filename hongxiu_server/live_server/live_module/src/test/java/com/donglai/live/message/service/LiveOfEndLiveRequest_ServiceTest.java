package com.donglai.live.message.service;

import com.donglai.common.constant.LineSourceConstant;
import com.donglai.live.BaseTest;
import com.donglai.live.entityBuilder.UserBuilder;
import com.donglai.live.message.services.live.LiveOfEndLiveRequest_Service;
import com.donglai.live.message.services.live.LiveOfEnterLiveRoomRequest_Service;
import com.donglai.live.message.services.live.LiveOfStartLiveRequest_Service;
import com.donglai.live.process.LiveProcess;
import com.donglai.model.db.service.live.RoomService;
import com.donglai.protocol.Constant;
import com.donglai.protocol.Live;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import static com.donglai.protocol.Constant.ResultCode.SUCCESS;

@ActiveProfiles("default")
public class LiveOfEndLiveRequest_ServiceTest extends BaseTest {
    @Autowired
    RoomService roomService;
    @Autowired
    UserBuilder userBuilder;
    @Autowired
    LiveProcess liveProcess;
    @Autowired
    LiveOfEnterLiveRoomRequest_Service enterLiveRoomRequest_service;
    @Autowired
    LiveOfStartLiveRequest_Service liveOfStartLiveRequest_service;
    @Autowired
    LiveOfEndLiveRequest_Service liveOfEndLiveRequest_service;

    @Test
    public void testEndLive() throws InterruptedException {
        startLive();
        Live.LiveOfEnterLiveRoomRequest.Builder request = Live.LiveOfEnterLiveRoomRequest.newBuilder().setRoomId(room.getId());
        var message = enterLiveRoomRequest_service.Process(user.getId(), buildMessageRequest(request.build()));
        Assert.assertEquals(SUCCESS, message.getResultCode());

        Live.LiveOfEndLiveRequest.Builder endLiveRequest = Live.LiveOfEndLiveRequest.newBuilder().setEndDelayTime(10000);
        message = liveOfEndLiveRequest_service.Process(user.getId(), buildMessageRequest(endLiveRequest.build()));
        Assert.assertEquals(SUCCESS, message.getResultCode());
    }

    public void startLive() {
        var request = buildMessageRequest(Live.LiveOfStartLiveRequest.newBuilder().setPattern(Constant.LivePattern.LIVE_AUDIO).setLiveTag(Constant.LiveTag.JIAOYOU)
                .setRoomTitle("title").setLiveLineCode(LineSourceConstant.TENCENT_LINE).build());
        var message = liveOfStartLiveRequest_service.Process(user.getId(), request);
        Assert.assertEquals(SUCCESS, message.getResultCode());
        Assert.assertTrue(roomService.roomIsLive(room.getId()));
    }
}
