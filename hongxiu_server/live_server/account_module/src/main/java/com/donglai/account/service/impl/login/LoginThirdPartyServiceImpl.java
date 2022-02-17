package com.donglai.account.service.impl.login;

import com.alibaba.fastjson.JSONObject;
import com.donglai.account.service.LoginService;
import com.donglai.common.constant.AccountRedisConstant;
import com.donglai.common.service.RedisService;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.dto.Pair;
import com.donglai.model.util.UserSessionUtil;
import com.donglai.protocol.Account;
import com.donglai.protocol.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.donglai.protocol.Constant.ResultCode.SUCCESS;
import static com.donglai.protocol.Constant.ResultCode.USER_NOT_FOUND;

/**
 * @author Moon
 * @date 2021-11-18 14:42
 */
@Service
public class LoginThirdPartyServiceImpl implements LoginService {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisService redisService;

    @Override
    public Pair<Constant.ResultCode, User> login(Account.AccountOfLoginRequest request, String loginIp) {
        //第三方登录token
        String token = request.getToken();
        JSONObject jsonObject = UserSessionUtil.deCodeToJSon(token);
        String accId = String.valueOf(jsonObject.get(UserSessionUtil.KEY_ACCOUNT_ID));
        Object redisToken = redisService.get(AccountRedisConstant.getLoginSession(accId));

        if (!Objects.equals(token, redisToken)) {
            return new Pair<>(USER_NOT_FOUND, null);
        }
        User byId = userService.findById(accId);
        if (Objects.isNull(byId)) {
            return new Pair<>(USER_NOT_FOUND, null);
        }
        return new Pair<>(SUCCESS, byId);
    }
}
