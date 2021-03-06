package com.donglai.account.message.service;

import com.donglai.account.message.producer.Producer;
import com.donglai.account.process.ModuleNoticeProcess;
import com.donglai.account.service.impl.platform.PlatformServiceFactory;
import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.common.util.CastUtil;
import com.donglai.common.util.StringUtils;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.common.UserService;
import com.donglai.protocol.Account;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.Live;
import com.donglai.protocol.message.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.donglai.account.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.MISSING_OR_ILLEGAL_PARAMETERS;
import static com.donglai.protocol.Constant.ResultCode.SUCCESS;

@Service("AccountOfLoginRequest")
@Slf4j
public class AccountOfLoginRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    Producer producer;
    @Autowired
    UserService userService;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        Map<KafkaMessage.ExtraParam, String> extraParam = CastUtil.cast(param[0]);
        String loginIp = Optional.ofNullable(extraParam).orElse(new HashMap<>()).getOrDefault(KafkaMessage.ExtraParam.IP,"");
        var request = message.getAccountOfLoginRequest();
        var platform = message.getPlatform();
        var replyMessage = Account.AccountOfLoginReply.newBuilder();
        if (StringUtils.isNullOrBlank(request.getAccountId())) {
            return buildReply(userId, replyMessage.build(), MISSING_OR_ILLEGAL_PARAMETERS, extraParam, message.getPlatform());
        }
        var platformService = PlatformServiceFactory.getPlatformService(platform);
        var loginResult = platformService.loginProcess(request,loginIp);
        var resultCode = loginResult.getLeft();
        if (Objects.equals(SUCCESS, resultCode)) {
            User user = loginResult.getRight();
            userId = user.getId();
            replyMessage.setUserInfo(user.toDetailProto());
            //1.??????
            producer.sendReply(userId, replyMessage.build(), resultCode, extraParam);
            //2.??????????????????????????????
            broadCastToUserApplyLiveUserResult(user, extraParam); //??????????????????????????????
            //3.????????????????????????????????????????????????????????????
            ModuleNoticeProcess.noticeLiveServerUserConnection(userId, extraParam);
            return null;
        } else {
            return buildReply(userId, replyMessage.build(), resultCode, extraParam, message.getPlatform());
        }
    }

    private void broadCastToUserApplyLiveUserResult(User user, Map<KafkaMessage.ExtraParam, String> param) {
        if (Objects.equals(Constant.UserType.TYPE_LIVEUSER_APPROVED, user.getUserType())
                || Objects.equals(Constant.UserType.TYPE_LIVE_APPROVE_FAILED, user.getUserType())) {
            log.info("send message to user apply LiveUser result");
            var build = Live.LiveOfApplyLiveUserResultBroadcastMessage.newBuilder().setIsPass(user.isLiveUser()).build();
            producer.sendReply(user.getId(), build, Constant.ResultCode.SUCCESS, param);
            Constant.UserType userType = user.isLiveUser() ? Constant.UserType.TYPE_LIVEUSER : Constant.UserType.NORMAL;
            user.setUserType(userType);
            userService.save(user);
        }
    }

    @Override
    public void Close(String s) {

    }
}
