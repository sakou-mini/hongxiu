package com.donglai.account.service.impl.login;

import com.donglai.account.process.LoginProcess;
import com.donglai.account.service.LoginService;
import com.donglai.common.util.StringUtils;
import com.donglai.model.db.entity.account.TouristLoginLog;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.live.TouristLoginLogService;
import com.donglai.model.dto.Pair;
import com.donglai.protocol.Account;
import com.donglai.protocol.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.donglai.protocol.Constant.ResultCode.SUCCESS;

/**
 * @author Moon
 * @date 2021-11-18 14:45
 */
@Service
public class LoginAccountServiceImpl implements LoginService {
    @Autowired
    private LoginProcess loginProcess;
    @Autowired
    private TouristLoginLogService touristLoginLogService;
    @Autowired
    private UserService userService;

    @Override
    public Pair<Constant.ResultCode, User> login(Account.AccountOfLoginRequest request, String loginIp) {
        User user = userService.findByAccountId(request.getAccountId());
        Constant.ResultCode resultCode = loginProcess.verifyAccountLogin(user, request.getPassword());
        if(!Objects.equals(SUCCESS,resultCode)) return new Pair<>(resultCode, user);
        //如果是游客添加登录记录
        if (user.isTourist()) {
            TouristLoginLog touristLoginLog = new TouristLoginLog(user.getId(),loginIp);
            touristLoginLogService.save(touristLoginLog);
        }
        if (Objects.isNull(user.getFirstLoginTime()) || StringUtils.isNullOrBlank(user.getFirstLoginIp())) {
            user.setFirstLoginTime(System.currentTimeMillis());
            user.setFirstLoginIp(loginIp);
        }
        return new Pair<>(resultCode, user);
    }
}
