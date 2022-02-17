package com.donglai.web.builder;

import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.util.IdGeneratedProcess;
import com.donglai.protocol.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Objects;

@Component
public class UserBuilder {
    @Value("${account.length}")
    private int accountLength;

    @Autowired
    IdGeneratedProcess idGeneratedProcess;
    @Autowired
    UserService userService;

    @Transactional
    public User createUser(String accountId, String password, String avatar, Constant.PlatformType platform) {
        User user = userService.findByAccountId(accountId);
        if (Objects.isNull(user)) {
            user = new User(accountId, accountId, password, "");
            user.setAvatarUrl(avatar);
            user.setPlatform(platform);
            user.setTourist(false);
            user = userService.save(user);
        }
        return user;
    }

}
