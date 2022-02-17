package com.donglai.model.entityBuilder;

import com.donglai.model.db.entity.common.PersonalSetting;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.common.PersonalSettingService;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.util.IdGeneratedProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UserEntityBuilder {
    private static final int ACCOUNT_LENGTH = 8;
    @Autowired
    IdGeneratedProcess idGeneratedProcess;
    @Autowired
    UserService userService;
    @Autowired
    PersonalSettingService personalSettingService;

    public User createUser(String accountId, String avatar,String nickname,boolean officialUser) {
        User user = userService.findByAccountId(accountId);
        if (Objects.isNull(user)) {
            user = new User(accountId, nickname, "", "");
            user.setAvatarUrl(avatar);
            user.setOfficialUser(officialUser);
            user = userService.save(user);
            initNewUserData(user);
        }
        return user;
    }

    public void initNewUserData(User user) {
        personalSettingService.save(new PersonalSetting(user.getId()));
    }
}
