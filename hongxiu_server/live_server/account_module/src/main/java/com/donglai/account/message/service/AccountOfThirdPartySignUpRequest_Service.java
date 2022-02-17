package com.donglai.account.message.service;

import com.donglai.account.entityBuilder.UserBuilder;
import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.common.service.RedisService;
import com.donglai.common.util.CastUtil;
import com.donglai.common.util.PasswordUtil;
import com.donglai.common.util.StringUtils;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.common.UserService;
import com.donglai.protocol.Account;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.donglai.account.util.MessageUtil.buildReply;

/**
 * @author Moon  此处为调用了第三方登录接口后，客户端发起
 * @date 2021-11-23 16:33
 */
@Service("AccountOfThirdPartySignUpRequest")
public class AccountOfThirdPartySignUpRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    private UserBuilder userBuilder;
    @Autowired
    private RedisService redisService;
    @Autowired
    private UserService userService;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        Map<KafkaMessage.ExtraParam, String> extraParam = CastUtil.cast(param[0]);
        var request = message.getAccountOfThirdPartySignUpRequest();
        var builder = Account.AccountOfThirdPartySignUpReply.newBuilder();
        String uuid = request.getUuid();
        String source = request.getSource();
        String avatar = request.getAvatarUrl();
        String nickname = request.getNickname();
        //校验参数 判断uuid是否已经注册过了
        if (Objects.isNull(uuid) || StringUtils.isNullOrBlank(nickname) || StringUtils.isNullOrBlank(avatar) || StringUtils.isNullOrBlank(avatar)) {
            return buildReply(userId, builder.build(), Constant.ResultCode.MISSING_OR_ILLEGAL_PARAMETERS);
        }
        User user = userService.findByUuid(request.getUuid());
        if(Objects.isNull(user)){
            String pwd = UUID.randomUUID().toString();
            String pwdMd5 = PasswordUtil.encodePassword(pwd);
            user = userBuilder.createUser(nickname, avatar, uuid, source, pwdMd5);
            user = userService.save(user);
        }
        String deCodePwd = PasswordUtil.decodePassword(user.getPassword());
        builder.setAccountId(user.getAccountId()).setPassword(deCodePwd);
        return buildReply(userId, builder.build(), Constant.ResultCode.SUCCESS,extraParam,message.getPlatform());
    }

    @Override
    public void Close(String s) {

    }
}
