package com.donglai.web.process.permission;

import com.donglai.web.constant.BackOfficeRole;
import com.donglai.web.constant.MenuType;
import com.donglai.web.db.backoffice.entity.Menu;
import com.donglai.web.db.backoffice.entity.Role;
import com.donglai.web.db.backoffice.entity.RoleMenu;
import com.donglai.web.db.backoffice.service.MenuService;
import com.donglai.web.db.backoffice.service.RoleMenuService;
import com.donglai.web.db.backoffice.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
public class RoleProcess {
    @Autowired
    RoleService roleService;
    @Autowired
    MenuService menuService;
    @Autowired
    RoleMenuService roleMenuService;

    /*清除角色数据*/
    public void cleanData(){
        roleService.deleteAll();
        roleMenuService.deleteAll();
    }

    /*1.初始化超级管理员角色*/
    public void initRootRole() {
        Role superRole = roleService.findByRoleName(BackOfficeRole.ROLE_ADMIN.name());
        if (superRole == null) {
            log.info("初始化了角色ROLE_ADMIN");
            superRole = Role.newInstance(BackOfficeRole.ROLE_ADMIN.name(),BackOfficeRole.ROLE_ADMIN.name());
            superRole = roleService.save(superRole);
        }
        initPlatformRole();
    }

    /*2.初始化超级管理员菜单角色*/
    public void initRootRoleMenu(Role role){
        //TODO 不应当每次都删除后重新添加。仅测试时使用
        //roleMenuService.deleteAll();
        List<Menu> menus = menuService.findAllByMenuType(MenuType.MENU_PAGE_SYSTEM);
        List<RoleMenu> roleMenus = menus.stream().filter(menu -> Objects.isNull(roleMenuService.findByRoleIdAndMenuId(role.getId(), menu.getId())))
                .map(menu -> RoleMenu.newInstance(role.getId(), menu.getId())).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(roleMenus)) return;
        roleMenuService.saveAll(roleMenus);
    }

    public void initPlatformRole() {
        Role role = roleService.findByRoleName(BackOfficeRole.ROLE_DUOCAI_PLATFORM.name());
        if (role == null) {
            log.info("初始化了角色ROLE_PLATFORM");
            role = Role.newInstance(BackOfficeRole.ROLE_DUOCAI_PLATFORM.name(),BackOfficeRole.ROLE_DUOCAI_PLATFORM.name());
            role = roleService.save(role);
        }
    }
}
