package com.donglai.account.service.impl.platform;

import com.donglai.account.entityBuilder.UserBuilder;
import com.donglai.account.process.LiveUserProcess;
import com.donglai.account.process.LoginProcess;
import com.donglai.account.service.PlatformService;
import com.donglai.common.constant.UserStatus;
import com.donglai.common.util.PasswordUtil;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.dto.Pair;
import com.donglai.protocol.Account;
import com.donglai.protocol.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.donglai.common.constant.PathConstant.DEFAULT_AVATAR_PATH;
import static com.donglai.protocol.Constant.ResultCode.*;

@Component
@Slf4j
public class SportsPlatformServiceImpl implements PlatformService {

    @Autowired
    LoginProcess loginProcess;
    @Autowired
    UserService userService;
    @Autowired
    UserBuilder userBuilder;
    @Autowired
    LiveUserProcess liveUserProcess;

    @Override
    public User register(Account.AccountOfRegisterRequest request, Constant.PlatformType platform) {
        String password = PasswordUtil.encodePassword(request.getPassword());
        User user = userBuilder.createUser(password, null, 0, DEFAULT_AVATAR_PATH, Constant.PlatformType.SPORT);
        liveUserProcess.becomeSimpleLiveUser(user,Constant.PlatformType.SPORT);
        return user;
    }

    @Override
    public Pair<Constant.ResultCode, User> loginProcess(Account.AccountOfLoginRequest request,String loginIp) {
        Constant.ResultCode resultCode;
        User user = userService.findByAccountId(request.getAccountId());
        if (Objects.isNull(user)) {
            resultCode = USER_NOT_FOUND;
        } else if (!Objects.equals(PasswordUtil.decodePassword(user.getPassword()), request.getPassword())) {
            resultCode = PASSWORD_ERROR;
        } else if(Objects.equals(user.getStatus(), UserStatus.BAN.getValue())) {
            resultCode = ACCOUNT_BANNED;
        } else {
            resultCode = SUCCESS;
            loginProcess.loginSuccess(user, loginIp);
        }
        return new Pair<>(resultCode, user);
    }

    @Override
    public String getPlatformDefaultAvatar() {
        return DEFAULT_AVATAR_PATH;
    }
}
