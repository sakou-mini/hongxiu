package com.donglai.web.process;

import com.donglai.web.constant.BackOfficeRole;
import com.donglai.web.db.backoffice.entity.Role;
import com.donglai.web.db.backoffice.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RoleProcess {
    @Autowired
    RoleService roleService;

    public void initRootRole() {
        Role role = roleService.findByRoleName(BackOfficeRole.ROLE_ADMIN.name());
        if (role == null) {
            log.info("初始化了角色ROLE_ADMIN");
            role = Role.newInstance(BackOfficeRole.ROLE_ADMIN.name());
            role = roleService.save(role);
        }
        initTestRole();
        initPlatformRole();
    }

    public void initTestRole() {
        Role role = roleService.findByRoleName(BackOfficeRole.ROLE_TEST.name());
        if (role == null) {
            log.info("初始化了角色ROLE_Test");
            role = Role.newInstance(BackOfficeRole.ROLE_TEST.name());
            role = roleService.save(role);
        }
    }

    public void initPlatformRole() {
        Role role = roleService.findByRoleName(BackOfficeRole.ROLE_DUOCAI_PLATFORM.name());
        if (role == null) {
            log.info("初始化了角色ROLE_PLATFORM");
            role = Role.newInstance(BackOfficeRole.ROLE_DUOCAI_PLATFORM.name());
            role = roleService.save(role);
        }
    }
}
