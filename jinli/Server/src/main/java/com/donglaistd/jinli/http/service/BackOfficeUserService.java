package com.donglaistd.jinli.http.service;

import com.donglaistd.jinli.database.dao.BackOfficeUserDaoService;
import com.donglaistd.jinli.database.entity.backoffice.BackOfficeRole;
import com.donglaistd.jinli.database.entity.backoffice.BackOfficeUser;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

import static com.donglaistd.jinli.constant.BackOfficeConstant.ROOT_ACCOUNT;
import static com.donglaistd.jinli.constant.BackOfficeConstant.ROOT_PWD;
import static com.donglaistd.jinli.util.StringNumberUtils.generateGameId;

@Component
public class BackOfficeUserService {
    public static final String Q_PLATFORM_ACCOUNT = "platformQ";
    public static final String T_PLATFORM_ACCOUNT = "platformAccount";


    @Autowired
    BackOfficeUserDaoService backOfficeUserDaoService;
    @Autowired
    PasswordEncoder passwordEncoder;

    public BackOfficeUser createBackOfficeUser(String accountName, String password, List<BackOfficeRole> role){
        long backOfficeCount = backOfficeUserDaoService.findAll().size();
        HashSet<BackOfficeRole> roleList = new HashSet<>(role);
        var pwd = passwordEncoder.encode(password);
        BackOfficeUser backOfficeUser = new BackOfficeUser(accountName,pwd);
        backOfficeUser.setId(generateGameId(backOfficeCount,5));
        backOfficeUser.setRole(roleList);
        return backOfficeUserDaoService.save(backOfficeUser);
    }

    public void createEmptyRoleBackOfficeUser(String id, String accountName, String password){
        var pwd = passwordEncoder.encode(password);
        BackOfficeUser backOfficeUser = new BackOfficeUser(accountName,pwd);
        backOfficeUser.setId(id);
        backOfficeUser.setRole(new HashSet<>());
        backOfficeUserDaoService.save(backOfficeUser);
    }

    public void initPlatformAccount() {
        resetNotOfficeBackOfficeUserRole();
        BackOfficeUser QPlatformUser = backOfficeUserDaoService.findByAccountName(Q_PLATFORM_ACCOUNT);
        if(QPlatformUser==null){
            String pwd = "asdqwe123";
            createBackOfficeUser(Q_PLATFORM_ACCOUNT, pwd, Lists.newArrayList(BackOfficeRole.PLATFORM_Q, BackOfficeRole.LOGIN));
        }
        BackOfficeUser TPlatformUser = backOfficeUserDaoService.findByAccountName(T_PLATFORM_ACCOUNT);
        if(TPlatformUser==null){
            String pwd = "123456";
            createBackOfficeUser(T_PLATFORM_ACCOUNT, pwd, Lists.newArrayList(BackOfficeRole.PLATFORM, BackOfficeRole.LOGIN));
        }

        BackOfficeUser rootUser = backOfficeUserDaoService.findByAccountName(ROOT_ACCOUNT);
        if(rootUser == null){
            List<BackOfficeRole> permissions = Lists.newArrayList(BackOfficeRole.ADMIN,BackOfficeRole.LOGIN);
            createBackOfficeUser(ROOT_ACCOUNT, ROOT_PWD, permissions);
        }
    }

    public void modifyBackOfficeUserPwd(BackOfficeUser backOfficeUser ,String password){
        backOfficeUser.setToken(passwordEncoder.encode(password));
        backOfficeUserDaoService.save(backOfficeUser);
    }

    public void resetNotOfficeBackOfficeUserRole(){
        backOfficeUserDaoService.resetBackOfficeUser(Lists.newArrayList(Q_PLATFORM_ACCOUNT, T_PLATFORM_ACCOUNT, ROOT_ACCOUNT));
    }
}
