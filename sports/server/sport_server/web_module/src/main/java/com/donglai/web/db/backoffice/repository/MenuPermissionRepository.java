package com.donglai.web.db.backoffice.repository;

import com.donglai.web.db.backoffice.entity.MenuPermission;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;

public interface MenuPermissionRepository extends MongoRepository<MenuPermission,String> {
    List<MenuPermission> findAllByMenuIdIn(Collection<String> menuIds);
}
