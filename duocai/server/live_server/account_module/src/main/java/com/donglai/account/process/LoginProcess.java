package com.donglai.account.process;

import com.donglai.common.util.PasswordUtil;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.common.QueueExecuteService;
import com.donglai.model.db.service.common.UserService;
import com.donglai.protocol.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.donglai.protocol.Constant.ResultCode.*;

@Slf4j
@Component
public class LoginProcess {
    @Autowired
    QueueExecuteService queueExecuteService;
    @Autowired
    UserService userService;


    public Constant.ResultCode verifyAccountLogin(User user, String loginPassword) {
        if (Objects.isNull(user)) {
            return USER_NOT_FOUND;
        } else if (!PasswordUtil.checkEncodePassword(loginPassword, user.getPassword())) {
            return PASSWORD_ERROR;
        }
        return SUCCESS;
    }

    public void loginSuccess(User user,String ip) {
        log.info("login SUCCESS for user:" + user.getAccountId());
        user.setLastLoginTime(System.currentTimeMillis());
        user.setIp(ip);
        userService.save(user);
    }
}
