package com.donglai.web.process;

import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.common.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.donglai.common.constant.CommonConstant.OFFICIAL_ACCOUNT;

@Component
@Slf4j
public class UserProcess {

    @Autowired
    UserService userService;

    public void initOfficialUser() {
        User officialUser = userService.findByAccountId(OFFICIAL_ACCOUNT);
        if (officialUser == null) {
            User user = new User(OFFICIAL_ACCOUNT, OFFICIAL_ACCOUNT, "", "");
            user.setTourist(false);
            user.setOfficialUser(true);
            userService.save(user);
            log.info("初始化了官方用户:" + user);
        }
    }
}
