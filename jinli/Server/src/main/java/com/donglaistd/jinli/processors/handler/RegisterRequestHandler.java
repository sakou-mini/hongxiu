package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.annotation.IgnoreAuth;
import com.donglaistd.jinli.builder.UserBuilder;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.service.statistic.UserDataStatisticsProcess;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

import static com.donglaistd.jinli.Constant.ResultCode.MISSING_OR_ILLEGAL_PARAMETERS;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;
import static com.donglaistd.jinli.util.StringUtils.checkAccountName;

@IgnoreAuth
@Component
public class RegisterRequestHandler extends MessageHandler {
    private static final Logger logger = Logger.getLogger(RegisterRequestHandler.class.getName());

    @Autowired
    private UserDaoService userDaoService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${tourist.name.prefix}")
    private String touristNamePrefix;

    @Autowired
    private DataManager dataManager;

    @Autowired
    UserBuilder userBuilder;

    @Autowired
    UserDataStatisticsProcess userDataStatisticsProcess;

    @Value("${player.init.gamecoin}")
    long initGameCoin;

    @Override
    public synchronized Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        var message = messageRequest.getRegisterRequest();
        var reply = Jinli.RegisterReply.newBuilder();
        String accountName = message.getAccountName();
        if (StringUtils.isNullOrBlank(accountName) || !checkAccountName(accountName)) {
            return buildReply(reply, MISSING_OR_ILLEGAL_PARAMETERS);
        }

        if (userDaoService.existByAccountNameCaseInsensitive(accountName)) {
            logger.warning("user already exist:" + accountName);
            return buildReply(reply, Constant.ResultCode.ACCOUNT_NAME_ALREADY_EXIST);
        }
        String password = message.getPassword();
        boolean isTourist = false;
        if (StringUtils.isNullOrBlank(password)) {
            isTourist = true;
        }
        String token = StringUtils.isNullOrBlank(password) ? "" : passwordEncoder.encode(password);
        String disPlayName = message.getDisplayName();
        User insertUser = userBuilder.createRegisterUser(accountName, disPlayName, token, initGameCoin, message.getMobileCode(), isTourist);
        reply.setToken(password);
        logger.info("new user register saved: accountName:<" + insertUser.getAccountName() + ">, display name:<" + insertUser.getDisplayName() + ">");
        userDataStatisticsProcess.totalRegisterUserDataStatistics(message,insertUser.getId(), insertUser.getPlatformType());
        return buildReply(reply, resultCode);
    }
}
