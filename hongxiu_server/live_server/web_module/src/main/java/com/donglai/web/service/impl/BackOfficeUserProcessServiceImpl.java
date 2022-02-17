package com.donglai.web.service.impl;

import com.donglai.web.db.backoffice.entity.BackOfficeUser;
import com.donglai.web.service.BackOfficeUserProcessService;
import com.donglai.web.web.dto.request.LoginRequest;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class BackOfficeUserProcessServiceImpl implements BackOfficeUserProcessService {


    @Override
    public BackOfficeUser loadUserDetailsByUserName(String username) throws AuthenticationException {
        return null;
    }

    @Override
    public Object login(LoginRequest loginRequest) {
        BackOfficeUser backOfficeUser;
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        UsernamePasswordToken pwdToken = new UsernamePasswordToken(loginRequest.getUsername(), loginRequest.getPassword());
        subject.login(pwdToken);
        return session.getId();
    }

    @Override
    public void logout() {
        Subject subject = SecurityUtils.getSubject();
        if (Objects.nonNull(subject))
            subject.logout();
    }
}
