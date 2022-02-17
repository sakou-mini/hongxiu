package com.donglai.live.message.services;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.common.util.StringUtils;
import com.donglai.live.entityBuilder.LiveUserBuilder;
import com.donglai.live.entityBuilder.RoomBuilder;
import com.donglai.live.message.producer.Producer;
import com.donglai.live.process.LiveUserProcess;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.live.LiveUser;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.live.LiveUserService;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.Live;
import com.donglai.protocol.message.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.donglai.live.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.*;

@Service("LiveOfApplyLiveUserRequest")
public class LiveOfApplyLiveUserRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    Producer producer;
    @Autowired
    LiveUserBuilder liveUserBuilder;
    @Autowired
    RoomBuilder roomBuilder;
    @Autowired
    LiveUserService liveUserService;
    @Autowired
    UserService userService;
    @Autowired
    LiveUserProcess liveUserProcess;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        Live.LiveOfApplyLiveUserRequest request = message.getLiveOfApplyLiveUserRequest();
        Live.LiveOfApplyLiveUserReply reply = Live.LiveOfApplyLiveUserReply.newBuilder().build();
        User user = userService.findById(userId);
        Constant.ResultCode resultCode;
        if (user.isTourist()) {
            resultCode = UNOFFICIAL_USER;
        } else if (!verifyBecomeLiveUserRequest(request)) {
            resultCode = MISSING_OR_ILLEGAL_PARAMETERS;
        } else {
            LiveUser liveUser = liveUserService.findByUserId(userId);
            if (Objects.nonNull(liveUser) && liveUser.isPassLiveUser()) {
                resultCode = REPEATED_APPLY_LIVEUSER;
            } else {
                resultCode = SUCCESS;
                liveUserProcess.applyLiveUser(request, userId, Constant.LiveUserStatus.LIVE_LIVE);
                //TODO  will delete
                liveUserProcess.broadCastUserApplyLiveUserResult(userId, true, null);
            }
        }
        return buildReply(userId, reply, resultCode);
    }

    private boolean verifyBecomeLiveUserRequest(Live.LiveOfApplyLiveUserRequest request) {
        return !StringUtils.isNullOrBlank(request.getRealName())
                && request.getGender() != null
                && !StringUtils.isNullOrBlank(request.getCountry())
                && !StringUtils.isNullOrBlank(request.getAddress())
                && !StringUtils.isNullOrBlank(request.getEmail())
                && !StringUtils.isNullOrBlank(request.getPhoneNumber())
                && !StringUtils.isNullOrBlank(request.getContactWay())
                && !StringUtils.isNullOrBlank(request.getBirthDay())
                && !StringUtils.isNullOrBlank(request.getBankName())
                && !StringUtils.isNullOrBlank(request.getBankCard());
    }

    @Override
    public void Close(String s) {
    }
}
