package com.donglaistd.jinli.service;

import com.donglaistd.jinli.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collection;
import java.util.Map;

@ActiveProfiles("test")
public class MenuRoleProcessTest extends BaseTest {
    @Autowired
    MenuRoleProcess menuRoleProcess;

    @Test
    public void menuRoleTest(){
        Map<String, Collection<ConfigAttribute>> menuRolePathPermissionMap = menuRoleProcess.getMenuRolePathPermissionMap();
        System.out.println(menuRolePathPermissionMap);
    }
}
