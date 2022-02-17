package com.donglaistd.jinli.service;

import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.service.redis.RedisService;
import com.donglaistd.jinli.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.donglaistd.jinli.constant.CacheNameConstant.getUserSessionKey;
@Component
public class LoginProcess {
    @Autowired
    RedisService redisService;
    @Autowired
    UserDaoService userService;

    public User findUserByAccount(String account){
        var user = userService.findByAccountName(account);
        if(user == null) user = userService.findById(account);
        if(user == null) user = userService.findByPhoneNumber(account);
        return user;
    }

    public String generatedAndSaveUserSession(User user){
        String userSession = SessionUtil.getUserSession(user);
        String userSessionKey = getUserSessionKey(user.getId());
        redisService.set(userSessionKey,userSession, TimeUnit.HOURS.toMillis(6));
        return userSession;
    }

}
