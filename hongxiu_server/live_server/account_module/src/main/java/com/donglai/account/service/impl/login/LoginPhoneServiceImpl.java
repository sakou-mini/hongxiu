package com.donglai.account.service.impl.login;

import com.donglai.account.service.LoginService;
import com.donglai.common.service.RedisService;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.dto.Pair;
import com.donglai.protocol.Account;
import com.donglai.protocol.Constant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.donglai.protocol.Constant.ResultCode.*;

/**
 * @author Moon
 * @date 2021-11-18 14:45
 */
@Service
public class LoginPhoneServiceImpl implements LoginService {
    @Autowired
    private RedisService redisService;
    @Autowired
    UserService userService;

    @Override
    public Pair<Constant.ResultCode, User> login(Account.AccountOfLoginRequest request, String loginIp) {
        //验证码
        String code = request.getCode();
        String phone = request.getPhone();
        Constant.ResultCode resultCode = SUCCESS;
        User user = userService.findByPhoneNumber(phone);
        if (StringUtils.isEmpty(code) || StringUtils.isEmpty(phone)) {
            resultCode = MISSING_OR_ILLEGAL_PARAMETERS;
        } else {
            //TODO get form redis
            //Object redisCode = redisService.get(RedisConstant.getPhoneMessageLogin(phone));
            Object redisCode = code;
            //校验验证码是否正确
            if (!Objects.equals(code, redisCode)) {
                resultCode = MISSING_OR_ILLEGAL_PARAMETERS;
            }
            if (Objects.isNull(user)) {
                resultCode = USER_NOT_FOUND;
            }
        }
        return new Pair<>(resultCode, user);
    }
}
