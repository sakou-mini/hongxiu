package com.donglai.account.entityBuilder;

import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.util.IdGeneratedProcess;
import com.donglai.protocol.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserBuilder {
    @Value("${account.length}")
    private int accountLength;

    @Value("${tourist.name.prefix}")
    String touristNamePrefix;

    @Autowired
    IdGeneratedProcess idGeneratedProcess;

    @Autowired
    UserService userService;

    @Transactional
    public User createUser(String password, String mobileCode, long gameCoin, String avatar, Constant.PlatformType platform) {
        String account = idGeneratedProcess.generatedId(User.class.getSimpleName(),accountLength);
        String nickname = touristNamePrefix + account;
        User user = new User(account, nickname, password, mobileCode);
        user.setCoin(new AtomicLong(gameCoin));
        user.setPlatform(platform);
        user.setAvatarUrl(avatar);
        user = userService.save(user);
        return user;
    }

    @Transactional
    public User createUser(String accountId, String password, String mobileCode,String avatar, Constant.PlatformType platform){
        User user = userService.findByAccountId(accountId);
        if(Objects.isNull(user)){
            String nickname = touristNamePrefix + accountId;
            user = new User(accountId, nickname, password, mobileCode);
            user.setAvatarUrl(avatar);
            user.setPlatform(platform);
            user = userService.save(user);
        }
        return user;
    }
}
