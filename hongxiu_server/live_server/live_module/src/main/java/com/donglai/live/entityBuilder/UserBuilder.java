package com.donglai.live.entityBuilder;

import com.donglai.model.db.entity.common.PersonalSetting;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.common.PersonalSettingService;
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

    @Autowired
    PersonalSettingService personalSettingService;

    @Transactional
    public User createUser(String password, String mobileCode, String avatar, long gameCoin, Constant.PlatformType platform) {
        String account = idGeneratedProcess.generatedId(User.class.getSimpleName(), accountLength);
        String nickname = touristNamePrefix + account;
        User user = new User(account, nickname, password, mobileCode);
        user.setCoin(new AtomicLong(gameCoin));
        user.setAvatarUrl(avatar);
        user.setPlatform(platform);
        user = userService.save(user);
        initNewUserData(user);
        return user;
    }

    @Transactional
    public User createUser(String accountId, String password, String mobileCode, String avatar, long gameCoin, Constant.PlatformType platform) {
        User user = userService.findByAccountId(accountId);
        if (Objects.isNull(user)) {
            String nickname = touristNamePrefix + accountId;
            user = new User(accountId, nickname, password, mobileCode);
            user.setAvatarUrl(avatar);
            user.setCoin(new AtomicLong(gameCoin));
            user.setPlatform(platform);
            user = userService.save(user);
            initNewUserData(user);
        }
        return user;
    }

    public void initNewUserData(User user) {
        personalSettingService.save(new PersonalSetting(user.getId()));
    }
}
