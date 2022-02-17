package com.donglaistd.jinli.service;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.database.dao.BackOfficeUserRepository;
import com.donglaistd.jinli.database.entity.backoffice.BackOfficeRole;
import com.donglaistd.jinli.database.entity.backoffice.BackOfficeUser;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;

import static com.donglaistd.jinli.database.entity.backoffice.BackOfficeRole.ADMIN;

public class BackOfficeUserDetailsServiceTest extends BaseTest {
    @Autowired
    BackOfficeUserDetailsService backOfficeUserDetailsService;
    @Autowired
    BackOfficeUserRepository backOfficeUserRepository;

    @Test
    public void TestCanGetBackOfficeUserDetails() {
        String username = "username";
        var user = new BackOfficeUser(username, "token");
        var roleSet = new HashSet<BackOfficeRole>();
        roleSet.add(BackOfficeRole.ADMIN);
        user.setRole(roleSet);
        backOfficeUserRepository.save(user);
        var userDetails = backOfficeUserDetailsService.loadUserByUsername(username);
        Assert.assertEquals(userDetails.getUsername(), username);
        Assert.assertEquals(userDetails.getPassword(), "token");
        Assert.assertEquals(userDetails.getAuthorities().size(), 1);
        Assert.assertEquals(userDetails.getAuthorities().iterator().next().getAuthority(), "ROLE_ADMIN");
    }
}
