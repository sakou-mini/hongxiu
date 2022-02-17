package com.donglai.account.entityBuilder;

import com.donglai.common.util.StringUtils;
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
import java.util.UUID;

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

    public void initNewUserData(User user) {
        personalSettingService.save(new PersonalSetting(user.getId()));
    }

    @Transactional
    public User createUser(String nickname, String avatar, String uuid, String source, String pwd) {
        String accountId = idGeneratedProcess.generatedId(User.class.getSimpleName(), 8);
        User user = new User(accountId, nickname, uuid, source, pwd, null, false);
        //设置头像
        user.setAvatarUrl(avatar);
        user.setCreateTime(System.currentTimeMillis());
        //入库User
        return userService.save(user);
    }

    @Transactional
    public User createUserByPhone(String password, long birthDay, String phone) {
        String account = idGeneratedProcess.generatedId(User.class.getSimpleName(), accountLength);
        User user = new User(account, account, password, "");
        user.setPhoneNumber(phone);
        user.setBirthday(birthDay);
        user.setTourist(false);
        user = userService.save(user);
        initNewUserData(user);
        return user;
    }

    @Transactional
    public User createUser(String accountId, String password, String mobileCode, String avatar, Constant.PlatformType platform) {
        User user = userService.findByAccountId(accountId);
        if (Objects.isNull(user)) {
            String nickname = touristNamePrefix + accountId;
            user = new User(accountId, nickname, password, mobileCode);
            user.setAvatarUrl(avatar);
            user.setPlatform(platform);
            user = userService.save(user);
            initNewUserData(user);
        }
        return user;
    }

    @Transactional
    public User createTourist(String mobileCode, String pwd) {
        String nickname = touristNamePrefix + StringUtils.generateStringName(userService.count());
        String account = UUID.randomUUID().toString();
        //String account = idGeneratedProcess.generatedId(User.class.getSimpleName(), accountLength);
        User user = new User(account, nickname, pwd, mobileCode);
        user.setCreateTime(System.currentTimeMillis());
        user.setTourist(true);
        return userService.save(user);
    }
}
