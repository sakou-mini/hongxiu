package com.donglai.web.process.permission;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.donglai.common.util.FileUtil;
import com.donglai.common.util.StringUtils;
import com.donglai.web.constant.MenuType;
import com.donglai.web.db.backoffice.entity.BackOfficeUser;
import com.donglai.web.db.backoffice.entity.Menu;
import com.donglai.web.db.backoffice.entity.Role;
import com.donglai.web.db.backoffice.entity.RoleMenu;
import com.donglai.web.db.backoffice.service.MenuService;
import com.donglai.web.db.backoffice.service.RoleMenuService;
import com.donglai.web.db.backoffice.service.RoleService;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    /*清除菜单数据*/
    public void cleanData(){
        menuService.deleteAll();
    }

    // =================init menu=======================
    @Transactional
    public void addRoleToMenu(Menu menu, Role role){
        RoleMenu roleMenu = RoleMenu.newInstance(role.getId(), menu.getId());
        roleMenuService.save(roleMenu);
        menuService.save(menu);
    }

    /*初始化json配置的 菜单*/
    public void initMenuDataByMenuJson() {
        List<Menu> allMenu = menuService.findAllByMenuType(MenuType.MENU_PAGE_SYSTEM);
        if(allMenu.isEmpty()) {
            log.info("初始化页面菜单");
            String menuStr = FileUtil.ReadFile(menuFilePath);
            JSONArray jsonObject = JSONArray.parseArray(menuStr);
            List<Menu> menuList = JSON.parseArray(jsonObject.toJSONString(), Menu.class);
            //分离所有菜单，分别存储所有的菜单，
            menuService.saveAll(menuList);
        }
    }


    //============getRoleMenu(目前最多支持2层菜单)============
    public List<Menu> getRoleMenuPageList(List<Role> roles,MenuType menuType){
        List<Menu> menuPageList = menuService.findMenusByMenuTypeAndMenuRole(menuType, roles);
        Map<String,Menu>  parentMap = menuPageList.stream().filter(menu -> StringUtil.isNullOrEmpty(menu.getParentId())).collect(Collectors.toMap(Menu::getId, menu -> menu));
        for (Menu menu : menuPageList) {
            if(!StringUtils.isNullOrBlank(menu.getParentId())){
                parentMap.get(menu.getParentId());
            }
        }
        ArrayList<Menu> menus = new ArrayList<>(parentMap.values());
        menus.sort(getParentMenuComparator());
        return menus;
    }

    public boolean hasMenuPermission(String path, BackOfficeUser backOfficeUser){
        List<Menu> menus= menuService.findByPath(path);
        if(CollectionUtils.isEmpty(menus)) return false;
        if(backOfficeUser.hasAdminRole()) return true;
        List<String> roleIds = roleMenuService.findRolesMenuList(backOfficeUser.getRoles()).stream().map(RoleMenu::getRoleId).collect(Collectors.toList());
        return backOfficeUser.getRoles().stream().anyMatch(role -> roleIds.contains(role.getId()));
    }

    public void resetMenu() {
        menuService.deleteAllByMenuType(MenuType.MENU_PAGE_SYSTEM);
        initMenuDataByMenuJson();
    }

    public List<Menu> getBackUserMenus(BackOfficeUser backOfficeUser){
        if(backOfficeUser.hasAdminRole())
            return menuService.getAllMenu().stream().sorted(getParentMenuComparator()).collect(Collectors.toList());
        List<String> menuIds = roleMenuService.findRolesMenuList(backOfficeUser.getRoles()).stream().map(RoleMenu::getMenuId).collect(Collectors.toList());
        return menuService.findByIds(menuIds).stream().sorted(getParentMenuComparator()).collect(Collectors.toList());
    }

    public List<Menu> getBackUserMenus(Role role){
        if(role.isAdminRole())
            return menuService.getAllMenu().stream().sorted(getParentMenuComparator()).collect(Collectors.toList());
        List<String> menuIds = roleMenuService.findByRoleId(role.getId()).stream().map(RoleMenu::getMenuId).collect(Collectors.toList());
        return menuService.findByIds(menuIds).stream().sorted(getParentMenuComparator()).collect(Collectors.toList());
    }

    //传递一个菜单id列表过来，自动填充其对应的父级菜单
    public List<String>  formatMenuNode(List<String> menuIds){
        List<Menu> menus = menuService.findByIds(menuIds);
        Set<String> groupIds = menus.stream().map(Menu::getGroupId).filter(groupId -> !StringUtils.isNullOrBlank(groupId)).collect(Collectors.toSet());
        menuIds.addAll(groupIds);
        return menuIds.stream().distinct().collect(Collectors.toList());
    }
}
