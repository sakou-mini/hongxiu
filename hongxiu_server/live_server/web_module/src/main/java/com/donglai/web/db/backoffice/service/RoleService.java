package com.donglai.web.db.backoffice.service;

import com.donglai.web.db.backoffice.entity.Role;
import com.donglai.web.db.backoffice.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    @Autowired
    RoleRepository roleRepository;

    public Role save(Role role) {
        return roleRepository.save(role);
    }

    public Role findByRoleName(String roleName) {
        return roleRepository.findByName(roleName);
    }

    public Role findByRoleId(String roleId) {
        return roleRepository.findById(roleId).orElse(null);
    }

    public List<Role> findByRoleIdIn(List<String> ids) {
        return roleRepository.findByIdIn(ids);
    }

    public Object saveAll(List<Role> roles) {
        return roleRepository.saveAll(roles);
    }

    public Object deleteByIdIn(List<String> ids) {
        return roleRepository.deleteByIdIn(ids);
    }
}
