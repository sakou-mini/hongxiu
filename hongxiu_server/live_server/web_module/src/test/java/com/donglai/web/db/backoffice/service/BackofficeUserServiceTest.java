package com.donglai.web.db.backoffice.service;

import com.donglai.web.WebBaseTest;
import com.donglai.web.db.backoffice.entity.BackOfficeUser;
import com.donglai.web.db.backoffice.entity.Role;
import com.donglai.web.response.PageInfo;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class BackofficeUserServiceTest extends WebBaseTest {
    @Autowired
    BackOfficeUserService backOfficeUserService;
    @Autowired
    RoleService roleService;

    @Test
    public void findByRoleId() {
        Role role1 = Role.newInstance("role1");
        role1 = roleService.save(role1);
        Role role2 = Role.newInstance("role2");
        role2 = roleService.save(role2);
        BackOfficeUser backOfficeUser1 = BackOfficeUser.newInstance("测试管理员", "pwd", "adminTest1", Lists.newArrayList(role1, role2));
        backOfficeUserService.save(backOfficeUser1);

        BackOfficeUser backOfficeUser2 = BackOfficeUser.newInstance("测试管理员2", "pwd", "adminTest2", Lists.newArrayList(role1, role2));
        backOfficeUserService.save(backOfficeUser2);

        PageInfo<BackOfficeUser> result = backOfficeUserService.pageQuery("1", true, 1, 10);
        Assert.assertEquals(2L, result.getContent().size());
    }
}
