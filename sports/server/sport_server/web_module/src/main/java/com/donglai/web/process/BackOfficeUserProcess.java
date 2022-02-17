package com.donglai.web.process;

import com.donglai.common.service.RedisService;
import com.donglai.web.constant.BackOfficeRole;
import com.donglai.web.constant.BackofficeDataBaseConstant;
import com.donglai.web.db.backoffice.entity.BackOfficeUser;
import com.donglai.web.db.backoffice.entity.Role;
import com.donglai.web.db.backoffice.service.BackOfficeUserService;
import com.donglai.web.db.backoffice.service.RoleService;
import com.donglai.web.util.MD5Utils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class BackOfficeUserProcess {
    @Value("${security.default.password}")
    private String defaultPwd;
    @Autowired
    BackOfficeUserService backOfficeUserService;
    @Autowired
    RoleService roleService;
    @Autowired
    RedisService redisService;

    /*清除后台账号数据*/
    public void cleanData(){
        backOfficeUserService.deleteAll();
        cleanAllSession();
    }

    //清除所有的session，直接清除redis
    public void cleanAllSession(){
        List<String> sessionKeys = new ArrayList<>(redisService.keys("shiro:session:*"));
        redisService.del(sessionKeys);
    }

    public void initRootUser() {
        var backOfficeUser = backOfficeUserService.findByUserName(BackofficeDataBaseConstant.SUPER_ADMIN);
        Role superRole = roleService.findByRoleName(BackOfficeRole.ROLE_ADMIN.name());
        if (Objects.isNull(backOfficeUser)) {
            String encodePwd = MD5Utils.setMd5Crytography(defaultPwd);
            backOfficeUser = BackOfficeUser.newInstance(BackofficeDataBaseConstant.SUPER_ADMIN, "123456", "超级管理员", Lists.newArrayList(superRole));
            backOfficeUser = backOfficeUserService.save(backOfficeUser);
            log.info("初始化了系统根用户 {}", backOfficeUser);
        }
    }

    public void initBackOfficeUserByNameAndRole(String userName, List<Role> roles, String password) {
        var backOfficeUser = backOfficeUserService.findByUserName(userName);
        if (Objects.isNull(backOfficeUser)) {
            String encodePwd = MD5Utils.setMd5Crytography(defaultPwd);
            backOfficeUser = BackOfficeUser.newInstance(userName, password, userName, roles);
            backOfficeUser = backOfficeUserService.save(backOfficeUser);
            log.info("初始化了用户 {}", backOfficeUser);
        }
    }
}
