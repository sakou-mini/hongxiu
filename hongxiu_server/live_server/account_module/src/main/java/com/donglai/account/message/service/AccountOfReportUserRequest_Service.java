package com.donglai.account.message.service;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.live.ReportUser;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.live.ReportUserService;
import com.donglai.protocol.Account;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.donglai.account.util.MessageUtil.buildReply;

/**
 * @author Moon
 * @date 2022-01-22 13:37
 */
@Service("AccountOfReportUserRequest")
public class AccountOfReportUserRequest_Service implements TopicMessageServiceI<String> {

    @Autowired
    private ReportUserService reportUserService;
    @Autowired
    private UserService userService;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        Account.AccountOfReportUserRequest request = message.getAccountOfReportUserRequest();
        Account.AccountOfReportUserReply.Builder builder = Account.AccountOfReportUserReply.newBuilder();
        if(StringUtils.isEmpty(request.getReason()) || StringUtils.isEmpty(request.getUserId())) {
            return buildReply(userId, builder, Constant.ResultCode.MISSING_OR_ILLEGAL_PARAMETERS);
        }

        ReportUser reportUser = ReportUser.newInstance(request.getUserId(), request.getReason());

        reportUser.setCreatedId(userId);

        reportUserService.save(reportUser);
        return buildReply(userId, builder, Constant.ResultCode.SUCCESS);
    }

    @Override
    public void Close(String s) {

    }
}
