package com.donglai.web.web.controller.system;

import com.donglai.web.constant.MenuType;
import com.donglai.web.db.backoffice.entity.BackOfficeUser;
import com.donglai.web.db.backoffice.entity.Menu;
import com.donglai.web.db.backoffice.service.BackOfficeUserService;
import com.donglai.web.process.MenuProcess;
import com.donglai.web.response.RestResponse;
import com.donglai.web.response.SuccessResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Api(value = "菜单管理", tags = "menuController")
@RequestMapping("/api/v1/menu")
@Slf4j
public class MenuController {
    @Autowired
    BackOfficeUserService backOfficeUserService;
    @Autowired
    MenuProcess menuProcess;

    @ApiOperation(value = "获取角色对应的菜单")
    @GetMapping(value = "/roleMenu")
    public RestResponse roleMenu() {
        BackOfficeUser backOfficeUser = (BackOfficeUser) SecurityUtils.getSubject().getPrincipals().getPrimaryPrincipal();
        Map<MenuType, List<Menu>> menuMap = new HashMap<>();
        List<Menu> systemMenu = menuProcess.getRoleMenuPageList(backOfficeUser.getRoles(), MenuType.MENU_PAGE_SYSTEM);
        menuMap.put(MenuType.MENU_PAGE_SYSTEM, systemMenu);
        return new SuccessResponse().withData(menuMap);
    }

    @ApiOperation(value = "校验菜单权限")
    @PostMapping(value = "/checkRoleMenu")
    public RestResponse roleMenuPermission(String path) {
        BackOfficeUser backOfficeUser = (BackOfficeUser) SecurityUtils.getSubject().getPrincipals().getPrimaryPrincipal();
        boolean result = menuProcess.hasMenuPermission(path, backOfficeUser);
        return new SuccessResponse().withData(result);
    }

    @ApiOperation(value = "重置菜单列表")
    @GetMapping(value = "/resetMenu")
    public RestResponse resetMenu() {
        menuProcess.resetMenu();
        return new SuccessResponse().withData("重置系统菜单成功");
    }
}
