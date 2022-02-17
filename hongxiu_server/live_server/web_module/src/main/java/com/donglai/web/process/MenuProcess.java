package com.donglai.web.process;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.donglai.common.util.FileUtil;
import com.donglai.common.util.StringUtils;
import com.donglai.web.constant.BackOfficeRole;
import com.donglai.web.constant.MenuType;
import com.donglai.web.db.backoffice.entity.BackOfficeUser;
import com.donglai.web.db.backoffice.entity.Menu;
import com.donglai.web.db.backoffice.entity.Role;
import com.donglai.web.db.backoffice.entity.RoleMenu;
import com.donglai.web.db.backoffice.service.MenuService;
import com.donglai.web.db.backoffice.service.RoleMenuService;
import com.donglai.web.db.backoffice.service.RoleService;
import com.google.common.collect.Lists;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.donglai.web.util.ComparatorUtil.getParentMenuComparator;

@Component
@Slf4j
public class MenuProcess {
    @Value("${menu.file.path}")
    private String menuFilePath;
    final MenuService menuService;
    final RoleMenuService roleMenuService;
    final RoleService roleService;

    public MenuProcess(MenuService menuService, RoleMenuService roleMenuService, RoleService roleService) {
        this.menuService = menuService;
        this.roleMenuService = roleMenuService;
        this.roleService = roleService;
    }

    // =================init menu=======================
    public void initMenu() {
        Menu menu = menuService.findByPath("/api/v1/backofficeUser/test2");
        Role rootRole = roleService.findByRoleName(BackOfficeRole.ROLE_TEST.name());
        if (menu == null) {
            menu = Menu.newInstance("/api/v1/backofficeUser/test2", "测试一下", MenuType.MENU_API);
            menu.setRoles(Lists.newArrayList(rootRole));
            addRoleToMenu(menu, rootRole);
        }
        initMenu2();
    }

    public void initMenu2() {
        Menu menu = menuService.findByPath("/api/v1/menu/roleMenu");
        Role rootRole = roleService.findByRoleName(BackOfficeRole.ROLE_ADMIN.name());
        Role rootRole2 = roleService.findByRoleName(BackOfficeRole.ROLE_TEST.name());
        if (menu == null) {
            menu = Menu.newInstance("/api/v1/menu/roleMenu", "菜单路径", MenuType.MENU_API);
            menu.setRoles(Lists.newArrayList(rootRole,rootRole2));
            addRoleToMenu(menu, rootRole);
        }
    }

    @Transactional
    public void addRoleToMenu(Menu menu, Role role) {
        menu.addRole(role);
        RoleMenu roleMenu = RoleMenu.newInstance(role.getId(), menu.getId());
        roleMenuService.save(roleMenu);
        menuService.save(menu);
    }

    public void initMenuDataByMenuJson() {
        //Role rootRole = roleService.findByRoleName(BackOfficeRole.ROLE_ADMIN.name());
        List<Menu> allMenu = menuService.findMenusByMenuTypeAndMenuRole(MenuType.MENU_PAGE_SYSTEM, null);
        if (allMenu.isEmpty()) {
            log.info("初始化页面菜单");
            String menuStr = FileUtil.ReadFile(menuFilePath);
            JSONObject jsonObject = JSONObject.parseObject(menuStr);
            List<Menu> giftLists = JSON.parseArray(jsonObject.getJSONArray("systemMenu").toJSONString(), Menu.class);
            //分离所有菜单，分别存储所有的菜单，
            List<Menu> menuList = getAllMenu(giftLists);
            menuService.saveAll(menuList);
        }
    }

    public List<Menu> getAllMenu(List<Menu> rootMenuList) {
        List<Menu> menuList = new ArrayList<>();
        for (Menu menu : rootMenuList) {
            menuList.addAll(getMenuList(menu, MenuType.MENU_PAGE_SYSTEM));
        }
        return menuList;
    }

    private List<Menu> getMenuList(Menu menu, MenuType menuType) {
        List<Menu> menuList = new ArrayList<>();
        menu.setMenuType(menuType);
        menuList.add(menu);
        if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
            for (Menu child : menu.getChildren()) {
                child.setParentId(menu.getId());
                child.setMenuType(menuType);
                menuList.addAll(getMenuList(child, menuType));
            }
            menu.getChildren().clear();
        }
        return menuList;
    }

    //============getRoleMenu(目前最多支持2层菜单)============
    public List<Menu> getRoleMenuPageList(List<Role> roles, MenuType menuType) {
        List<Menu> menuPageList = menuService.findMenusByMenuTypeAndMenuRole(menuType, roles);
        Map<String, Menu> parentMap = menuPageList.stream().filter(menu -> StringUtil.isNullOrEmpty(menu.getParentId())).collect(Collectors.toMap(Menu::getId, menu -> menu));
        for (Menu menu : menuPageList) {
            if (!StringUtils.isNullOrBlank(menu.getParentId())) {
                parentMap.get(menu.getParentId()).addChildrenMenu(menu);
            }
        }
        ArrayList<Menu> menus = new ArrayList<>(parentMap.values());
        menus.sort(getParentMenuComparator());
        return menus;
    }

    public boolean hasMenuPermission(String path, BackOfficeUser backOfficeUser) {
        Menu menu = menuService.findByPath(path);
        if (Objects.isNull(menu)) return false;
        if (menu.getRoles().isEmpty()) return true;
        else return CollectionUtils.retainAll(menu.getRoles(), backOfficeUser.getRoles()).size() > 0;
    }

    public void resetMenu() {
        menuService.deleteAllByMenuType(MenuType.MENU_PAGE_SYSTEM);
        initMenuDataByMenuJson();
    }
}
