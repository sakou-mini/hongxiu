package com.donglai.live.message.services.gift;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.live.process.GiftProcess;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.Live;
import com.donglai.protocol.message.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.donglai.live.util.MessageUtil.buildReply;

@Service("LiveOfSendGiftRequest")
@Slf4j
public class LiveOfSendGiftRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    GiftProcess giftProcess;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getLiveOfSendGiftRequest();
        var replyBuilder = Live.LiveOfSendGiftReply.newBuilder();
        String giftId = request.getGiftId();
        int giftNum = request.getGiftNum();
        Constant.ResultCode resultCode = giftProcess.verifySendGiftParam(request.getReceiveUserId(), giftId, giftNum);
        if (Objects.equals(Constant.ResultCode.SUCCESS, resultCode)) {
            resultCode = giftProcess.sendGift(userId, request.getReceiveUserId(), giftId, giftNum);
            if (Objects.equals(Constant.ResultCode.SUCCESS, resultCode)) {
                log.info("sendGift success");
            }
        }
        return buildReply(userId, replyBuilder.build(), resultCode);
    }

    @Override
    public void Close(String s) {

    }
}
