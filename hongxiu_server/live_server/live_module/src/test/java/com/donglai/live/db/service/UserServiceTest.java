package com.donglai.live.db.service;

import com.donglai.live.BaseTest;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.common.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserServiceTest extends BaseTest {
    @Autowired
    UserService userService;


    @Test
    public void test() {
        User user = new User();
        user.setAccountId("455575");
        user.setNickname("nickName");
        user = userService.save(user);
    }
}
