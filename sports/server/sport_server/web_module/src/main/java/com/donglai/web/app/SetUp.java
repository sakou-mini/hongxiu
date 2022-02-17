package com.donglai.web.app;

import com.donglai.common.constant.DomainArea;
import com.donglai.model.db.entity.common.H5DomainConfig;
import com.donglai.model.db.service.common.H5DomainConfigService;
import com.donglai.web.db.backoffice.entity.Role;
import com.donglai.web.db.backoffice.service.RoleService;
import com.donglai.web.process.BackOfficeUserProcess;
import com.donglai.web.process.UserProcess;
import com.donglai.web.process.permission.MenuProcess;
import com.donglai.web.process.permission.PermissionProcess;
import com.donglai.web.process.permission.RoleProcess;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.donglai.web.constant.BackOfficeRole.ROLE_DUOCAI_PLATFORM;
import static com.donglai.web.constant.BackofficeDataBaseConstant.PLATFORM;

@Component
@Slf4j
public class SetUp implements CommandLineRunner {
    @Value("${local.domain.url}")
    private String defaultH5Domain;

    @Autowired
    RoleProcess roleProcess;
    @Autowired
    BackOfficeUserProcess backOfficeUserProcess;
    @Autowired
    MenuProcess menuProcess;
    @Autowired
    PermissionProcess permissionProcess;
    @Autowired
    UserProcess userProcess;
    @Autowired
    RoleService roleService;
    @Autowired
    H5DomainConfigService h5DomainConfigService;

    @Override
    public void run(String... args)  {
        //FIXME 生产环境务必注释
        cleanAllBackOfficeData();
        //菜单权限初始化
        initMenuAndPermission();
        //后台账号、角色初始化
        initRoleAndBackOfficeUser();
        //web 域名初始化
        initH5Domain();
    }

    private void cleanAllBackOfficeData(){
        menuProcess.cleanData();
        permissionProcess.cleanData();
        roleProcess.cleanData();
        backOfficeUserProcess.cleanData();
    }

    private void initMenuAndPermission(){
        menuProcess.initMenuDataByMenuJson();
        permissionProcess.initPermissionByPermissionJSon();
    }

    private void initRoleAndBackOfficeUser() {
        roleProcess.initRootRole();
        //1.init adminUser
        backOfficeUserProcess.initRootUser();
        //2.init platformUser
        Role role = roleService.findByRoleName(ROLE_DUOCAI_PLATFORM.name());
        backOfficeUserProcess.initBackOfficeUserByNameAndRole(PLATFORM, Lists.newArrayList(role), "92358toplkiw");

        //3.TODO 初始化官方用户(删除)
        userProcess.initOfficialUser();
    }


    private void initH5Domain() {
        h5DomainConfigService.deleteAll();
        List<H5DomainConfig> allDomain = h5DomainConfigService.findAll();
        if(CollectionUtils.isEmpty(allDomain)) {
            log.info("初始化H5 默认域名");
            H5DomainConfig h5DomainConfig = H5DomainConfig.newInstance(defaultH5Domain, DomainArea.INLAND);
            h5DomainConfigService.save(h5DomainConfig);
        }
    }

}
