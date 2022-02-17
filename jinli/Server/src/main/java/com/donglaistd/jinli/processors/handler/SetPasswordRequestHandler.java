package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.util.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.Constant.ResultCode.NOT_MODIFY_DISPLAY_NAME;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class SetPasswordRequestHandler extends MessageHandler{
    private static final Logger logger = Logger.getLogger(SetPasswordRequestHandler.class.getName());
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Value("${max.password.length}")
    private int maxPasswordLength;

    @Value("${min.password.length}")
    private int minPasswordLength;
    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.SetPasswordReply.Builder reply = Jinli.SetPasswordReply.newBuilder();
        if (StringUtils.isNullOrBlank(user.getPhoneNumber())) return buildReply(reply, NOT_BIND_PHONE);
        if(user.getModifyNameCount() <=0 ) return buildReply(reply, NOT_MODIFY_DISPLAY_NAME);
        Jinli.SetPasswordRequest request = messageRequest.getSetPasswordRequest();
        var password = request.getPassword();
        if (StringUtils.isNullOrBlank(password) || password.length() > maxPasswordLength || password.length() < minPasswordLength) {
            return buildReply(reply, MISSING_OR_ILLEGAL_PARAMETERS);
        }
        if (!user.isTourist()) {
            return buildReply(reply, Constant.ResultCode.NOT_TOURIST_BIND_ACCOUNT);
        }
        password = passwordEncoder.encode(password);
        user.setToken(password);
        user.setTourist(false);
        dataManager.saveUser(user);
        logger.info("set user password: accountName:<" + user.getAccountName() + ">, display name:<" + user.getDisplayName() + ">");
        return buildReply(reply, resultCode);
    }
}
