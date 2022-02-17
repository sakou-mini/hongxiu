package com.donglai.account.message.service;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.entity.account.UserFeedback;
import com.donglai.model.db.service.account.UserFeedbackService;
import com.donglai.protocol.Account;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import org.springframework.stereotype.Service;

import static com.donglai.account.util.MessageUtil.buildReply;

@Service("AccountOfFeedbackRequest")
public class AccountOfFeedbackRequest_Service implements TopicMessageServiceI<String> {
    final UserFeedbackService userFeedbackService;

    public AccountOfFeedbackRequest_Service(UserFeedbackService userFeedbackService) {
        this.userFeedbackService = userFeedbackService;
    }

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var feedbackRequest = message.getAccountOfFeedbackRequest();
        var reply = Account.AccountOfFeedbackReply.newBuilder();
        UserFeedback userFeedback = UserFeedback.newInstance(userId, feedbackRequest.getContent(), feedbackRequest.getPicturesList(),
                feedbackRequest.getAppVersion(), feedbackRequest.getMobileModel());
        userFeedbackService.save(userFeedback);
        return buildReply(userId, reply.build(), Constant.ResultCode.SUCCESS);
    }

    @Override
    public void Close(String s) {

    }
}
