package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.util.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

import static com.donglaistd.jinli.Constant.ResultCode.MISSING_OR_ILLEGAL_PARAMETERS;
import static com.donglaistd.jinli.Constant.ResultCode.PASSWORD_ERROR;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class ChangePasswordRequestHandler extends MessageHandler {
    private static final Logger logger = Logger.getLogger(ChangePasswordRequestHandler.class.getName());

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${max.password.length}")
    private int maxPasswordLength;

    @Value("${min.password.length}")
    private int minPasswordLength;


    @Override
    public synchronized Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.ChangePasswordRequest request = messageRequest.getChangePasswordRequest();
        Jinli.ChangePasswordReply.Builder reply = Jinli.ChangePasswordReply.newBuilder();
        boolean matches = passwordEncoder.matches(user.getToken(), request.getExistingPassword());
        if (!matches) {
            buildReply(reply, PASSWORD_ERROR);
        }
        String newPassword = request.getNewPassword();
        if (StringUtils.isNullOrBlank(newPassword) || newPassword.length() > maxPasswordLength || newPassword.length() < minPasswordLength) {
            return buildReply(reply, MISSING_OR_ILLEGAL_PARAMETERS);
        }
        newPassword = passwordEncoder.encode(newPassword);
        user.setToken(newPassword);
        dataManager.saveUser(user);
        logger.info("Save user new password: accountName:<" + user.getAccountName() + ">, display name:<" + user.getDisplayName() + ">");
        return buildReply(reply, resultCode);
    }
}
