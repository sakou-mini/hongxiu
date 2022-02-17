package com.donglaistd.jinli.service;

import com.donglaistd.jinli.database.entity.backoffice.BackOfficeRole;
import com.donglaistd.jinli.database.entity.backoffice.MenuRole;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.json.JSONArray;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.donglaistd.jinli.constant.BackOfficeConstant.ROLE_PREFIX;
import static com.donglaistd.jinli.constant.CacheNameConstant.MENU_ROLE_KEY;

@Service
public class MenuRoleProcess {
    private static final long TIMEOUT_TIME = 60000;

    @Autowired
    RedisTemplate<String, List<MenuRole>> redisTemplate;

    private List<MenuRole> initMenu() {
        try {
            var file = new File("config/json/role.json");
            JSONArray jsonObject = new JSONArray(new JSONTokener(new FileInputStream(file)));
            var configs = new ObjectMapper().readValue(jsonObject.toString(),MenuRole[].class);
            return  Lists.newArrayList(configs);
        } catch (FileNotFoundException | JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<MenuRole> getMenuRoleList(){
        List<MenuRole> menuRoles = redisTemplate.opsForValue().get(MENU_ROLE_KEY);
        if(menuRoles == null){
            menuRoles = initMenu();
            redisTemplate.opsForValue().set(MENU_ROLE_KEY, menuRoles, TIMEOUT_TIME, TimeUnit.MILLISECONDS);
        }
        return menuRoles;
    }

    public List<MenuRole> getMenuRoleByRoles(Set<BackOfficeRole> backOfficeRoles){
        List<MenuRole> menuRoleList = getMenuRoleList();
        for (MenuRole menuRole : menuRoleList) {
            setMenuRoleCheckedByRoles(menuRole, backOfficeRoles);
        }
        return menuRoleList;
    }

    public void setMenuRoleCheckedByRoles(MenuRole menuRole, Set<BackOfficeRole> roles) {
        if (roles.contains(BackOfficeRole.valueOf(menuRole.getId())) || roles.contains(BackOfficeRole.ADMIN)) {
            menuRole.setChecked(true);
            List<MenuRole> children = menuRole.getChildren();
            if(children!=null && !children.isEmpty()){
                for (MenuRole childMenuRole : children) {
                    setMenuRoleCheckedByRoles(childMenuRole, roles);
                }
            }
        }
    }

    public Map<String, Collection<ConfigAttribute>> getMenuRolePathPermissionMap(){
        Map<String, Collection<ConfigAttribute>> permissionMap = new HashMap<>();
        List<MenuRole> menuRoleList = getMenuRoleList();
        for (MenuRole menuRole : menuRoleList) {
            setMenuPathRole(menuRole, permissionMap);
        }
        return permissionMap;
    }

    public void setMenuPathRole(MenuRole menuRole, Map<String, Collection<ConfigAttribute>> pathPermissionMap){
        for (String path : menuRole.getPath()) {
            pathPermissionMap.computeIfAbsent(path, k -> new ArrayList<>()).add(new SecurityConfig(ROLE_PREFIX + menuRole.getId()));
        }
        List<MenuRole> childrens = menuRole.getChildren();
        if(childrens!=null && !childrens.isEmpty()){
            for (MenuRole childMenuRole : childrens) {
                setMenuPathRole(childMenuRole, pathPermissionMap);
            }
        }
    }
}
