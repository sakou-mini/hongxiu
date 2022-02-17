package com.donglai.web.db.backoffice.service;

import com.donglai.web.db.backoffice.entity.MenuPermission;
import com.donglai.web.db.backoffice.repository.MenuPermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class MenuPermissionService {
    @Autowired
    MenuPermissionRepository repository;



    public List<MenuPermission> findAll(){
        return repository.findAll();
    }

    public List<MenuPermission> findByMenuIds(Collection<String> menuIds){
        return repository.findAllByMenuIdIn(menuIds);
    }

    public MenuPermission save(MenuPermission menuPermission) {
        return repository.save(menuPermission);
    }

    public List<MenuPermission> saveAll(List<MenuPermission> menuPermissions) {
        return repository.saveAll(menuPermissions);
    }

    public void delete(MenuPermission menuPermission){
        repository.delete(menuPermission);
    }

    public void deleteAll(){
        repository.deleteAll();
    }
}
