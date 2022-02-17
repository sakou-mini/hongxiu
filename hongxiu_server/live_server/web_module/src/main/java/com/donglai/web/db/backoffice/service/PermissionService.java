package com.donglai.web.db.backoffice.service;

import com.donglai.web.db.backoffice.entity.Permission;
import com.donglai.web.db.backoffice.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class PermissionService {
    @Autowired
    PermissionRepository repository;

    @Transactional
    public List<Permission> saveAll(List<Permission> permissions){
        return repository.saveAll(permissions);
    }

    public Permission save(Permission permission) {
        return repository.save(permission);
    }
}
