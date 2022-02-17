package com.donglai.live.message.service;

import com.donglai.common.constant.LineSourceConstant;
import com.donglai.live.BaseTest;
import com.donglai.live.message.services.live.LiveOfStartLiveRequest_Service;
import com.donglai.model.db.service.live.RoomService;
import com.donglai.protocol.Constant;
import com.donglai.protocol.Live;
import com.donglai.protocol.message.KafkaMessage;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class LiveOfStartLivedLiveTest extends BaseTest {
    @Autowired
    LiveOfStartLiveRequest_Service liveOfStartLiveRequest_service;
    @Autowired
    RoomService roomService;

    @Test
    public void startLiveTest() {
        var request = buildMessageRequest(Live.LiveOfStartLiveRequest.newBuilder().setPattern(Constant.LivePattern.LIVE_AUDIO).setLiveTag(Constant.LiveTag.JIAOYOU)
                .setRoomTitle("title").setLiveLineCode(LineSourceConstant.TENCENT_LINE).build());
        KafkaMessage.TopicMessage message = liveOfStartLiveRequest_service.Process(user.getId(), request);
        Assert.assertEquals(Constant.ResultCode.SUCCESS, message.getResultCode());
        Assert.assertTrue(roomService.roomIsLive(room.getId()));
    }
}
