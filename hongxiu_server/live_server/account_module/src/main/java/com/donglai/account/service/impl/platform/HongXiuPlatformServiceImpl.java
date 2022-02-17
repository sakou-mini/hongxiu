package com.donglai.account.service.impl.platform;

import com.donglai.account.entityBuilder.UserBuilder;
import com.donglai.account.process.LoginProcess;
import com.donglai.account.service.LoginService;
import com.donglai.account.service.PlatformService;
import com.donglai.account.service.impl.login.LoginServiceFactory;
import com.donglai.common.util.PasswordUtil;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.dto.Pair;
import com.donglai.protocol.Account;
import com.donglai.protocol.Constant;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.donglai.protocol.Constant.ResultCode.SUCCESS;

@Component
public class HongXiuPlatformServiceImpl implements PlatformService {
    public static final String DEFAULT_AVATAR = "/defaultImage/avatar/default/img_avatar.png";
    public static final String DEFAULT_ROOM_IMAGE = "/defaultImage/Account/img_Account_room.png";

    private final LoginServiceFactory loginServiceFactory;
    private final UserBuilder userBuilder;
    private final LoginProcess loginProcess;

    public HongXiuPlatformServiceImpl(LoginServiceFactory loginServiceFactory, UserBuilder userBuilder, LoginProcess loginProcess) {
        this.loginServiceFactory = loginServiceFactory;
        this.userBuilder = userBuilder;
        this.loginProcess = loginProcess;
    }


    @Override
    public User register(Account.AccountOfRegisterRequest request, Constant.PlatformType platform) {
        return userBuilder.createTourist(request.getMobileCode(), PasswordUtil.encodePassword(request.getPassword()));
    }

    @Override
    public Pair<Constant.ResultCode, User> loginProcess(Account.AccountOfLoginRequest request, String loginIp) {
        //TODO 登录方式  ,目前仅支持 账号密码登录
        Constant.LoginType loginType = request.getLoginType();
        LoginService loginService = loginServiceFactory.getLoginService(Constant.LoginType.ACCOUNT_LOGIN);
        if (Objects.isNull(loginService)) {
            return new Pair<>(Constant.ResultCode.MISSING_OR_ILLEGAL_PARAMETERS, null);
        }
        Pair<Constant.ResultCode, User> loginResult = loginService.login(request, loginIp);
        if (Objects.equals(SUCCESS, loginResult.getLeft())) {
            loginProcess.loginSuccess(loginResult.getRight(), loginIp);
        }
        return loginResult;
    }

    @Override
    public String getPlatformDefaultAvatar(Constant.PlatformType platform) {
        return null;
    }
}
