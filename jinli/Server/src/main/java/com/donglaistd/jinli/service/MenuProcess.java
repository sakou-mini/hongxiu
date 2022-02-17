package com.donglaistd.jinli.service;

import com.donglaistd.jinli.database.entity.backoffice.BackOfficeRole;
import com.donglaistd.jinli.database.entity.backoffice.BackOfficeUser;
import com.donglaistd.jinli.database.entity.system.Menu;
import com.donglaistd.jinli.util.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service
public class MenuProcess {
    private static final String MENU_KEY="MENU";
    private static final long TIMEOUT_TIME = 60000;
    @Autowired
    RedisTemplate<String, List<Menu>> menuTemplate;

    private static final Logger LOGGER = Logger.getLogger(MenuProcess.class.getName());

    public List<Menu> menuList = new ArrayList<>();

    public List<Menu> initMenu(){
        List<Menu> menus = menuTemplate.opsForValue().get(MENU_KEY);
        if(menus!=null) return menus;
        try {
            var file = new File("config/json/menu.json");
            JSONObject jsonObject = new JSONObject(new JSONTokener(new FileInputStream(file)));
            var configs = new ObjectMapper().readValue(jsonObject.getJSONArray("menuList").toString(), Menu[].class);
            List<Menu> menusList = Lists.newArrayList(configs);
            menuTemplate.opsForValue().set(MENU_KEY,menusList,TIMEOUT_TIME,TimeUnit.MILLISECONDS);
            return menusList;
        } catch (FileNotFoundException | JsonProcessingException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<Menu> getMenuByBackOfficeUser(BackOfficeUser backOfficeUser) {
        Set<BackOfficeRole> userRoles = backOfficeUser.getRoles();
        List<Menu> parentMenus = initMenu();
        List<Menu> userParentMenu = Lists.newArrayList();
        for (Menu parentMenu : parentMenus) {
            if(!hasMenuPermission(parentMenu.getRole(),userRoles)) continue;
            //获取侧边栏菜单
            List<Menu> sidebar = parentMenu.getSidebar();
            List<Menu> userSidebar = new ArrayList<>();
            if(sidebar!=null){
                List<Menu> removedSidebarMenu = new ArrayList<>();
                for (Menu sidebarMenu : sidebar) {
                    if(hasMenuPermission(sidebarMenu.getRole(),userRoles)){
                        List<Menu> childMenus = sidebarMenu.getChild();
                        if(childMenus!=null && !childMenus.isEmpty()){
                            List<Menu> userChildMenus = new ArrayList<>(childMenus.size());
                            for (Menu childMenu : childMenus) {
                                if(hasMenuPermission(childMenu.getRole(),userRoles)){
                                    userChildMenus.add(childMenu);
                                }
                            }
                            sidebarMenu.setChild(null);
                            if(!userChildMenus.isEmpty()){
                                sidebarMenu.setChild(userChildMenus);
                                userSidebar.add(sidebarMenu);
                            } else {
                                removedSidebarMenu.add(sidebarMenu);
                                //LOGGER.info("sidebar  子菜单已丢失所有权限，不纳入菜单 ："+sidebarMenu);
                            }
                        } else {
                            userSidebar.add(sidebarMenu);
                        }
                    }else{
                        removedSidebarMenu.add(sidebarMenu);
                    }
                }
                parentMenu.getSidebar().removeAll(removedSidebarMenu);
                //setSideBar
                if(!userSidebar.isEmpty()){
                    boolean b = setMenuUrlAndNameBySideBar(parentMenu, userSidebar.get(0));
                    if(!b){
                        List<Menu> child = userSidebar.get(0).getChild();
                        if(child!=null && !child.isEmpty()) setMenuUrlAndNameBySideBar(parentMenu, child.get(0));
                    }
                    userParentMenu.add(parentMenu);
                }
            }
        }
        return userParentMenu;
    }

    private boolean setMenuUrlAndNameBySideBar(Menu parentMenu ,Menu childrenMenu){
        if(childrenMenu.getName()==null || childrenMenu.getUrl()==null) return false;
        if(!StringUtils.isNullOrBlank(parentMenu.getName()) && !StringUtils.isNullOrBlank(parentMenu.getUrl())){
            parentMenu.setUrl(childrenMenu.getUrl());
            parentMenu.setName(childrenMenu.getName());
            return true;
        }
        return false;
    }

    private boolean hasMenuPermission(List<BackOfficeRole> menuRoles, Set<BackOfficeRole> userRoles){
        if(menuRoles.isEmpty() || userRoles.contains(BackOfficeRole.ADMIN)) return true;
        else return CollectionUtils.retainAll(menuRoles, userRoles).size()>0;
    }

    public List<Menu> getAllChildrenMenuList(){
        List<Menu> rootMenuList = initMenu();
        List<Menu> menuList = new ArrayList<>();
        for (Menu menu : rootMenuList) {
            menuList.addAll(getMenuList(menu,false,menu.getRole()));
        }
        return menuList;
    }

    private List<Menu> getMenuList(Menu menu,boolean isChildren,List<BackOfficeRole> parentRoles){
        List<BackOfficeRole> parentRole = new ArrayList<>();
        if(menu.getRole()!=null){
            parentRole = menu.getRole();
        }
        List<Menu> menuList = new ArrayList<>();
        if(menu.getSidebar() !=null && !menu.getSidebar().isEmpty()){
            for (Menu sidebar : menu.getSidebar()) {
                if(sidebar.getChild() != null && !sidebar.getChild().isEmpty()){
                    for (Menu child : sidebar.getChild()) {
                        menuList.addAll(getMenuList(child,true,parentRoles));
                    }
                    sidebar.getChild().clear();
                }else{
                    if(parentRoles!=null) sidebar.addAllRole(parentRoles);
                    menuList.add(sidebar);
                }
            }
            menu.getSidebar().clear();
        }else if(isChildren && !StringUtils.isNullOrBlank(menu.getUrl())){
            if(parentRoles!=null) menu.addAllRole(parentRoles);
            menuList.add(menu);
        }
        return menuList;
    }
}
