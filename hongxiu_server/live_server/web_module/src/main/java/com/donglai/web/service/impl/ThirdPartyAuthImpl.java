package com.donglai.web.service.impl;

import com.donglai.common.constant.AccountRedisConstant;
import com.donglai.common.service.RedisService;
import com.donglai.common.util.PasswordUtil;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.util.UserSessionUtil;
import com.donglai.web.builder.UserBuilder;
import com.donglai.web.service.ThirdPartyAuth;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.utils.UuidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Moon
 * @date 2021-10-26 17:00
 */

@Service
@Slf4j
public class ThirdPartyAuthImpl implements ThirdPartyAuth {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private UserBuilder userBuilder;


    @Override
    public AuthResponse getUserLoginInfo(String id) {
        id = id.replaceAll("#", "");
        log.info("查询登录消息{}",id);
        Object res = redisService.get(id);
        if (Objects.isNull(res)) {
            return new AuthResponse(1000, "登录超时", null);
        }
        return new AuthResponse(200, "验证成功", res);
    }

    @Override
    public String login(Object data) {
        AuthUser userInfo = (AuthUser) data;
        String uuid = String.valueOf(userInfo.getUuid());
        String source = String.valueOf(userInfo.getSource());
        String avatar = String.valueOf(userInfo.getAvatar());
        String nickname = String.valueOf(userInfo.getNickname());
        User byUuid = userService.findByUuid(uuid);
        //查询是否有该用户
        ////自动绑定
        //if (Objects.isNull(byUuid)) {
        //    //生成添加对象
        //    byUuid = new User();
        //    byUuid.setUuid(uuid);
        //    byUuid.setSource(source);
        //    byUuid.setAvatarUrl(avatar);
        //    byUuid.setNickname(nickname);
        //    redisService.set(AccountRedisConstant.getBindKey(uuid), uuid, 10);

        //    //提示需要第三方平台绑定账号才能登录
        //    return new AuthResponse(1000, "平台账号需要绑定第三方平台!方可登录", byUuid);
        //}
        Map<String, Object> res = new HashMap<>(3);
        if (Objects.isNull(byUuid)) {
            String pwd = UuidUtils.getUUID();
            String pwdMd5 = PasswordUtil.encodePassword(pwd);
            byUuid = userBuilder.createUser(nickname, avatar, uuid, source, pwdMd5);
            res.put("firstLogin", true);
            res.put("pwd", pwd);
        } else {
            res.put("pwd", PasswordUtil.decodePassword(byUuid.getPassword()));
        }
        //登录
        //生成token
        String userSession = UserSessionUtil.getUserSession(byUuid);
        res.put("token", userSession);
        res.put("userInfo", byUuid);
        redisService.set(AccountRedisConstant.getUserInfoSession(byUuid.getAccountId()), res, 20);
        return "&infoId=" + AccountRedisConstant.getUserInfoSession(byUuid.getAccountId());
    }
}
