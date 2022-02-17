package com.donglai.live.message.service;

import com.donglai.model.db.entity.common.User;
import com.donglai.live.BaseTest;
import com.donglai.common.constant.LineSourceConstant;
import com.donglai.model.db.entity.live.Room;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.live.RoomService;
import com.donglai.live.entityBuilder.UserBuilder;
import com.donglai.live.message.services.live.LiveOfEnterLiveRoomRequest_Service;
import com.donglai.live.message.services.live.LiveOfQuitRoomRequest_Service;
import com.donglai.live.message.services.live.LiveOfStartLiveRequest_Service;
import com.donglai.live.process.LiveProcess;
import com.donglai.live.util.LiveRedisUtil;
import com.donglai.protocol.Constant;
import com.donglai.protocol.Live;
import com.donglai.protocol.message.KafkaMessage;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.donglai.protocol.Constant.ResultCode.ROOM_NOT_LIVE;
import static com.donglai.protocol.Constant.ResultCode.SUCCESS;

public class LiveRoomTest extends BaseTest {
    @Autowired
    LiveOfStartLiveRequest_Service liveOfStartLiveRequest_service;
    @Autowired
    RoomService roomService;
    @Autowired
    UserBuilder userBuilder;
    @Autowired
    LiveProcess liveProcess;
    @Autowired
    UserService userService;

    @Autowired
    LiveOfEnterLiveRoomRequest_Service enterLiveRoomRequest_service;
    @Autowired
    LiveOfQuitRoomRequest_Service liveOfQuitRoomRequest_service;

    @Test
    public void TestEnterRoomRoom(){
        startLive();
        User audience1 = createUser();
        Live.LiveOfEnterLiveRoomRequest.Builder request = Live.LiveOfEnterLiveRoomRequest.newBuilder().setRoomId("10000");
        KafkaMessage.TopicMessage message = enterLiveRoomRequest_service.Process(audience1.getId(), buildMessageRequest(request.build()));
        Assert.assertEquals(ROOM_NOT_LIVE,message.getResultCode());
        request.setRoomId(room.getId());
        message = enterLiveRoomRequest_service.Process(audience1.getId(), buildMessageRequest(request.build()));
        Assert.assertEquals(SUCCESS,message.getResultCode());
        message = enterLiveRoomRequest_service.Process(user.getId(), buildMessageRequest(request.build()));
        Assert.assertEquals(SUCCESS,message.getResultCode());
        Room room = liveProcess.getOnlineRoomById(this.room.getId());
        Assert.assertEquals(2,room.getAudiences().size());
        Assert.assertEquals(room.getId(),LiveRedisUtil.getUserEnterRoomRecord(audience1.getId()));
        Assert.assertEquals(room.getId(),LiveRedisUtil.getUserEnterRoomRecord(user.getId()));
    }

    @Test
    public void TestQuitRoom(){
        //start live adn enter room
        //
        startLive();
        User audience1 = createUser();
        Live.LiveOfEnterLiveRoomRequest.Builder request = Live.LiveOfEnterLiveRoomRequest.newBuilder().setRoomId("10000");
        var message = enterLiveRoomRequest_service.Process(audience1.getId(), buildMessageRequest(request.build()));
        Assert.assertEquals(ROOM_NOT_LIVE,message.getResultCode());
        request.setRoomId(room.getId());
        message = enterLiveRoomRequest_service.Process(audience1.getId(), buildMessageRequest(request.build()));
        Assert.assertEquals(SUCCESS,message.getResultCode());
        message = enterLiveRoomRequest_service.Process(user.getId(), buildMessageRequest(request.build()));
        Assert.assertEquals(SUCCESS,message.getResultCode());
        Room room = liveProcess.getOnlineRoomById(this.room.getId());
        Assert.assertEquals(2,room.getAudiences().size());

        //quitRoom
        Live.LiveOfQuitRoomRequest.Builder quitRoomRequest = Live.LiveOfQuitRoomRequest.newBuilder().setRoomId(room.getId());
        var quitRoomMessage = liveOfQuitRoomRequest_service.Process(audience1.getId(), buildMessageRequest(quitRoomRequest.build()));
        Assert.assertEquals(SUCCESS,quitRoomMessage.getResultCode());
        room = liveProcess.getOnlineRoomById(this.room.getId());
        Assert.assertEquals(1,room.getAudiences().size());
    }

    public void startLive(){
        var request = buildMessageRequest(Live.LiveOfStartLiveRequest.newBuilder().setPattern(Constant.LivePattern.LIVE_AUDIO).setLiveTag(Constant.LiveTag.JIAOYOU)
                .setRoomTitle("title").setLiveLineCode(LineSourceConstant.TENCENT_LINE).build());
        var message = liveOfStartLiveRequest_service.Process(user.getId(), request);
        Assert.assertEquals(SUCCESS, message.getResultCode());
        Assert.assertTrue( roomService.roomIsLive(room.getId()));
    }

    public User createUser(){
        User user = userBuilder.createUser("pwd", "1","avatar",0, Constant.PlatformType.DUOCAI);
        return userService.save(user);
    }

}
