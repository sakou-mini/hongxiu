package com.donglai.web.process;

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

import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class BackOfficeUserProcess {
    @Value("${security.default.password}")
    private String defaultPwd;
    @Autowired
    private BackOfficeUserService backOfficeUserService;
    @Autowired
    RoleService roleService;

    public void initRootUser() {
        var backOfficeUser = backOfficeUserService.findByUserName(BackofficeDataBaseConstant.SUPER_ADMIN);
        Role role1 = roleService.findByRoleName(BackOfficeRole.ROLE_ADMIN.name());
        Role role2 = roleService.findByRoleName(BackOfficeRole.ROLE_TEST.name());
        if (Objects.isNull(backOfficeUser)) {
            String encodePwd = MD5Utils.setMd5Crytography(defaultPwd);
            backOfficeUser = BackOfficeUser.newInstance(BackofficeDataBaseConstant.SUPER_ADMIN, "123456", "超级管理员", Lists.newArrayList(role1, role2));
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
