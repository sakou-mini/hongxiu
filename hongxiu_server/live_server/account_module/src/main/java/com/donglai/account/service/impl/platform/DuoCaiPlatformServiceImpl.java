package com.donglai.account.service.impl.platform;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.donglai.account.entityBuilder.UserBuilder;
import com.donglai.account.process.LiveUserProcess;
import com.donglai.account.process.LoginProcess;
import com.donglai.account.service.PlatformService;
import com.donglai.account.util.HttpUtil;
import com.donglai.common.util.HashUtils;
import com.donglai.common.util.PasswordUtil;
import com.donglai.common.util.StringUtils;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.dto.DuoCaiUserInfoDTO;
import com.donglai.model.dto.Pair;
import com.donglai.protocol.Account;
import com.donglai.protocol.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.donglai.protocol.Constant.ResultCode.*;

@Component
@Slf4j
public class DuoCaiPlatformServiceImpl implements PlatformService {
    public static final String DEFAULT_AVATAR = "/defaultImage/duocai/icon.png";
    public static final String DUOCAI_API_KEY = "eccbc87e4b5ce2fe28308fd9f2a7baf3";
    public static final String DUOCAI_API_URL = "https://manycai01.net/api/lt/userinfo";
    @Autowired
    LoginProcess loginProcess;
    @Autowired
    UserService userService;
    @Autowired
    UserBuilder userBuilder;
    @Autowired
    LiveUserProcess liveUserProcess;

    @Override
    public User register(Account.AccountOfRegisterRequest AccountOfRegisterRequest, Constant.PlatformType platform) {
        return null;
    }

    @Override
    public Pair<Constant.ResultCode, User> loginProcess(Account.AccountOfLoginRequest request, String loginIp) {
        Constant.ResultCode resultCode;
        User user = userService.findByAccountId(request.getAccountId());
        if (Objects.isNull(user)) {
            resultCode = USER_NOT_FOUND;
        } else if (!Objects.equals(PasswordUtil.decodePassword(user.getPassword()), request.getPassword())) {
            resultCode = PASSWORD_ERROR;
        } else {
            resultCode = SUCCESS;
            loginProcess.loginSuccess(user, loginIp);
        }
        return new Pair<>(resultCode, user);
    }

    private User initDuoCaiUserAndBecomeLiveUser(DuoCaiUserInfoDTO duoCaiUserInfoDTO) {
        Constant.PlatformType platform = Constant.PlatformType.DUOCAI;
        User user = userService.findByAccountId(duoCaiUserInfoDTO.getUsername());
        if (user == null) {
            user = userBuilder.createUser(duoCaiUserInfoDTO.getUsername(), duoCaiUserInfoDTO.getNickname(), "", DEFAULT_AVATAR, platform);
            user.setOtherId(duoCaiUserInfoDTO.getId());
        } else {
            user.setNickname(duoCaiUserInfoDTO.getNickname());
        }
        if (StringUtils.isNullOrBlank(user.getLiveUserId())) liveUserProcess.becomeSimpleLiveUser(user, platform);
        return user;
    }

    private DuoCaiUserInfoDTO requestDuoCaiUserInfo(String account, String password) {
        Map<String, String> header = new HashMap<>();
        header.put("Api-Key", DUOCAI_API_KEY);
        Map<String, Object> param = new HashMap<>();
        param.put("username", account);
        param.put("password", HashUtils.getMd5Hash(password));
        JSONObject jsonObject = HttpUtil.postFormData(header, param, DUOCAI_API_URL);
        if (Objects.isNull(jsonObject) || !Objects.equals("0000", jsonObject.getString("status"))) {
            log.warn("request result failed by :{}", jsonObject);
            return null;
        }
        return JSON.parseObject(jsonObject.getJSONObject("data").toJSONString(), DuoCaiUserInfoDTO.class);
    }

    @Override
    public String getPlatformDefaultAvatar(Constant.PlatformType platform) {
        return DEFAULT_AVATAR;
    }
}
