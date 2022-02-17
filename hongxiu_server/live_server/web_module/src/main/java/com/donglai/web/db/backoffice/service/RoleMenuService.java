package com.donglai.web.db.backoffice.service;

import com.donglai.web.db.backoffice.entity.RoleMenu;
import com.donglai.web.db.backoffice.repository.RoleMenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleMenuService {

    @Autowired
    RoleMenuRepository roleMenuRepository;

    public RoleMenu findByRoleIdAndMenuId(String roleId, String menuId) {
        return roleMenuRepository.findByRoleIdAndMenuId(roleId, menuId);
    }

    public RoleMenu save(RoleMenu roleMenu) {
        return roleMenuRepository.save(roleMenu);
    }

    public List<RoleMenu> findByMenuId(String menuId) {
        return roleMenuRepository.findByMenuId(menuId);
    }

    public List<RoleMenu> findAll() {
        return roleMenuRepository.findAll();
    }

    public List<RoleMenu> findByRoleId(String roleId) {
        return roleMenuRepository.findByRoleId(roleId);
    }
}
