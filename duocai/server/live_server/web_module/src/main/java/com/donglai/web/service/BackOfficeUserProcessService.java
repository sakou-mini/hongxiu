package com.donglai.web.service;

import com.donglai.web.db.backoffice.entity.BackOfficeUser;
import com.donglai.web.response.PageInfo;
import com.donglai.web.web.dto.request.BackOfficeUserFindListRequest;
import com.donglai.web.web.dto.request.LoginRequest;
import org.apache.shiro.authc.AuthenticationException;

public interface BackOfficeUserProcessService {
    BackOfficeUser loadUserDetailsByUserName(String username) throws AuthenticationException;

    Object login(LoginRequest loginRequest);

    void logout();
}
