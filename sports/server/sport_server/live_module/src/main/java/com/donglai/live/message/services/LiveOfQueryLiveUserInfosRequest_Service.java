package com.donglai.live.message.services;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.live.process.LiveUserProcess;
import com.donglai.model.db.entity.live.LiveUser;
import com.donglai.model.db.service.live.LiveUserService;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.Live;
import com.donglai.protocol.message.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.donglai.live.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.LIVEUSER_NOT_EXIT;
import static com.donglai.protocol.Constant.ResultCode.SUCCESS;

@Service("LiveOfQueryLiveUserInfosRequest")
@Slf4j
public class LiveOfQueryLiveUserInfosRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    LiveUserService liveUserService;
    @Autowired
    LiveUserProcess liveUserProcess;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getLiveOfQueryLiveUserInfosRequest();
        var replyBuilder = Live.LiveOfQueryLiveUserInfosReply.newBuilder();
        LiveUser liveUser;
        Constant.ResultCode resultCode = SUCCESS;
        for (String requestUserId : request.getUserIdsList()) {
            liveUser = liveUserService.findByUserId(requestUserId);
            if (Objects.isNull(liveUser)) {
                resultCode = LIVEUSER_NOT_EXIT;
                log.warn("not found liveUser by userId {}", requestUserId);
                break;
            } else {
                resultCode = SUCCESS;
                replyBuilder.addLiveUserInfos(liveUserProcess.buildLiveUserInfo(liveUser));
            }
        }
        return buildReply(userId, replyBuilder.build(), resultCode);

    }

    @Override
    public void Close(String userId) {

    }
}
