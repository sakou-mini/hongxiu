package com.donglai.web.db.backoffice.service;

import com.donglai.web.WebBaseTest;
import com.donglai.web.constant.BackOfficeRole;
import com.donglai.web.db.backoffice.entity.BackOfficeUser;
import com.donglai.web.db.backoffice.entity.Role;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class BackOfficeUserServiceTest extends WebBaseTest {
    @Autowired
    BackOfficeUserService backOfficeUserService;
    @Autowired
    RoleService roleService;
    @Test
    public void test(){
        Role role = roleService.findByRoleName(BackOfficeRole.ROLE_ADMIN.name());
        List<BackOfficeUser> results = backOfficeUserService.findByHasRoles(Lists.newArrayList(role));
        System.out.println(results);
    }
}
