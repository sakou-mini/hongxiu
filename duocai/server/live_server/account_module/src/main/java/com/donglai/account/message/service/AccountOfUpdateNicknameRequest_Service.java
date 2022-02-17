package com.donglai.account.message.service;

import com.donglai.account.process.UserProcess;
import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.common.util.TimeUtil;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.live.ModifyNicknameRecordService;
import com.donglai.protocol.Account;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.donglai.account.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.*;

@Service("AccountOfUpdateNicknameRequest")
public class AccountOfUpdateNicknameRequest_Service implements TopicMessageServiceI<String> {
    @Value("${week.update.nickname.max.num}")
    private int weekOfModifyNicknameCount;
    @Value("${min.nick.name.length}")
    private int minDisplayNameLength;
    @Value("${max.nick.name.length}")
    private int maxDisplayNameLength;

    @Autowired
    ModifyNicknameRecordService modifyNicknameRecordService;
    @Autowired
    UserService userService;
    @Autowired
    UserProcess userProcess;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getAccountOfUpdateNicknameRequest();
        var reply = Account.AccountOfUpdateNicknameReply.newBuilder();
        long weekStartTime = TimeUtil.getFirstDayOfCurrentWeeks();
        long modifyNicknameCount = modifyNicknameRecordService.countModifyPasswordNumByTimes(userId, weekStartTime, System.currentTimeMillis());
        String nickname = request.getNickname();
        User user = userService.findById(userId);
        Constant.ResultCode resultCode;
        if (modifyNicknameCount >= weekOfModifyNicknameCount) {
            resultCode = OVER_NICK_MAX_MODIFY_COUNT;
        } else if (nickname.length() < minDisplayNameLength || nickname.length() > maxDisplayNameLength) {
            resultCode = NICKNAME_ILLEGAL;
        } else {
            resultCode = SUCCESS;
            user.setNickname(nickname);
            userService.save(user);
            reply.setUserInfo(userProcess.buildUerDetailInfo(user));
        }
        return buildReply(userId, reply.build(), resultCode);
    }

    @Override
    public void Close(String userId) {

    }
}
