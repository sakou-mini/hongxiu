package com.donglai.web.process.permission;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.donglai.common.util.CombineBeansUtil;
import com.donglai.common.util.FileUtil;
import com.donglai.common.util.StringUtils;
import com.donglai.model.dto.Pair;
import com.donglai.web.db.backoffice.entity.MenuPermission;
import com.donglai.web.db.backoffice.entity.Permission;
import com.donglai.web.db.backoffice.entity.Role;
import com.donglai.web.db.backoffice.entity.RoleMenu;
import com.donglai.web.db.backoffice.service.MenuPermissionService;
import com.donglai.web.db.backoffice.service.PermissionService;
import com.donglai.web.db.backoffice.service.RoleMenuService;
import com.donglai.web.dto.PermissionJSONConfig;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.donglai.web.constant.BackOfficeRole.ROLE_ADMIN;

/**
 *
 */
@Component
@Log4j
public class PermissionProcess {
    @Value("${permission.file.path}")
    private String permissionPath;

    @Autowired
    PermissionService permissionService;
    @Autowired
    MenuPermissionService menuPermissionService;
    @Autowired
    RoleMenuService roleMenuService;

    public void cleanData(){
        permissionService.deleteAll();
        menuPermissionService.deleteAll();
    }

    /*1.初始化权限 菜单关联*/
    public void initPermissionByPermissionJSon(){
        List<Permission> permissionList = permissionService.findAll();
        if(CollectionUtils.isEmpty(permissionList)){
            log.info("初始化权限");
            String menuStr = FileUtil.ReadFile(permissionPath);
            JSONArray jsonObject = JSONArray.parseArray(menuStr);

            List<PermissionJSONConfig> jsonConfigs = JSON.parseArray(jsonObject.toJSONString(), PermissionJSONConfig.class);
            List<Permission> permissions = jsonConfigs.stream().map(PermissionProcess::permissionJSONConfigToPermission).collect(Collectors.toList());

            permissions = permissionService.saveAll(permissions);
            Map<String, String> menuPermission = jsonConfigs.stream().collect(Collectors.toMap(PermissionJSONConfig::getApiPath, PermissionJSONConfig::getMenuId));

            //关联权限和菜单
            String menuId;
            List<MenuPermission> menuPermissions = new ArrayList<>(menuPermission.size());
            for (Permission permission : permissions) {
                menuId = menuPermission.get(permission.getApiPath());
                if(StringUtils.isNullOrBlank(menuId)) continue;
                menuPermissions.add(MenuPermission.newInstance(menuId, permission.getId()));
            }
            menuPermissionService.saveAll(menuPermissions);
        }
    }

    private static Permission permissionJSONConfigToPermission(PermissionJSONConfig config) {
        CombineBeansUtil<Permission> combineBeansUtil = new CombineBeansUtil<>(Permission.class);
        return combineBeansUtil.combineBeans(config);
    }

    /*2.获取所有的权限配置*/
    public List<Pair<String,String>> getAllPermissionPath(){
        List<Permission> allPermission = permissionService.findAll();
        return allPermission.stream().map(permission -> new Pair<>(permission.getApiPath(), permission.getFormatAuth())).collect(Collectors.toList());
    }

    /*3.获取对应角色的所有权限*/
    public Set<String> getRolesPermission(List<Role> roles){
        //如果是超级管理员账号。则给出所有的权限
        if(roles.stream().anyMatch(role -> Objects.equals(role.getName(),ROLE_ADMIN.name()))){
            return permissionService.findAll().stream().map(Permission::getAuth).collect(Collectors.toSet());
        }
        //1.剔除掉未启用的权限
        roles = roles.stream().filter(Role::isEnabled).collect(Collectors.toList());
        //2.找出角色对应的菜单
        Set<String> menuIds = roleMenuService.findRolesMenuList(roles).stream().map(RoleMenu::getMenuId).collect(Collectors.toSet());
        //3.找出菜单对应的权限
        Set<String> permissionIds = menuPermissionService.findByMenuIds(menuIds).stream().map(MenuPermission::getPermissionId).collect(Collectors.toSet());
        return permissionService.findByIds(permissionIds).stream().map(Permission::getAuth).collect(Collectors.toSet());
    }
}
