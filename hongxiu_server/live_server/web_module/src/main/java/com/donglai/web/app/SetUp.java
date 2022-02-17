package com.donglai.web.app;

import com.donglai.web.db.backoffice.entity.Role;
import com.donglai.web.db.backoffice.service.RoleService;
import com.donglai.web.process.*;
import com.donglai.web.util.MockUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static com.donglai.web.constant.BackOfficeRole.ROLE_DUOCAI_PLATFORM;
import static com.donglai.web.constant.BackofficeDataBaseConstant.PLATFORM;

@Component
@Slf4j
@Order(value = 2)
public class SetUp implements CommandLineRunner {
    @Autowired
    RoleProcess roleProcess;
    @Autowired
    BackOfficeUserProcess backOfficeUserProcess;
    @Autowired
    MenuProcess menuProcess;
    @Autowired
    UserProcess userProcess;
    @Autowired
    RoleService roleService;
    @Autowired
    BlogsProcess blogsProcess;
    @Autowired
    MockUtil mockUtil;

    @Override
    public void run(String... args) throws Exception {
        initBackOfficeUser();
        blogsProcess.initBlogsLabelConfig();
   /*     mockUtil.mockReportVideo();
        mockUtil.mockReportComment();
        mockUtil.mockReportUser();
        mockUtil.mockFeedBack();*/

    }

    public void initBackOfficeUser() {
        roleProcess.initRootRole();
        backOfficeUserProcess.initRootUser();
        //init platformUser
        Role role = roleService.findByRoleName(ROLE_DUOCAI_PLATFORM.name());
        backOfficeUserProcess.initBackOfficeUserByNameAndRole(PLATFORM, Lists.newArrayList(role), "92358toplkiw");
        //init menu
        menuProcess.initMenu();
        menuProcess.initMenuDataByMenuJson();
        userProcess.initOfficialUser();
        initDuocaiRole();
    }

    public void initDuocaiRole() {
        Role role = roleService.findByRoleName(ROLE_DUOCAI_PLATFORM.name());
        if (role == null) {
            log.info("初始化了角色ROLE_DUOCAI_PLATFORM");
            role = Role.newInstance(ROLE_DUOCAI_PLATFORM.name());
            role = roleService.save(role);
        }
    }

}
