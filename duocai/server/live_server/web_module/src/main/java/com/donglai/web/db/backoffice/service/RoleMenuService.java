package com.donglai.web.db.backoffice.service;

import com.donglai.web.db.backoffice.entity.Role;
import com.donglai.web.db.backoffice.entity.RoleMenu;
import com.donglai.web.db.backoffice.repository.RoleMenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleMenuService {

    @Autowired
    RoleMenuRepository repository;

    public RoleMenu findByRoleIdAndMenuId(String roleId, String menuId){
        return repository.findByRoleIdAndMenuId(roleId, menuId);
    }

    public RoleMenu save(RoleMenu roleMenu){
        return repository.save(roleMenu);
    }

    public List<RoleMenu> saveAll(Iterable<RoleMenu>  roleMenus){
        return repository.saveAll(roleMenus);
    }

    public List<RoleMenu> findByMenuId(String menuId) {
        return repository.findByMenuId(menuId);
    }

    public List<RoleMenu> findAll() {
        return repository.findAll();
    }

    public List<RoleMenu> findRolesMenuList(List<Role> roles){
        List<String> roleIds = roles.stream().map(Role::getId).collect(Collectors.toList());
        return repository.findByRoleIdIn(roleIds);
    }

    public List<RoleMenu> findByRoleId(String roleId) {
        return repository.findByRoleId(roleId);
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public void deleteByRolesId(List<String> roleIds){
        repository.deleteAllByRoleIdIn(roleIds);
    }

    public void deleteByRoleId(String roleId){
        repository.deleteByRoleId(roleId);
    }
}
