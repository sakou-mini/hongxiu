package com.donglai.web.db.backoffice.repository;

import com.donglai.web.db.backoffice.entity.RoleMenu;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RoleMenuRepository extends MongoRepository<RoleMenu, Long> {
    List<RoleMenu> findByMenuId(String menuId);

    List<RoleMenu> findByRoleId(String roleId);

    RoleMenu findByRoleIdAndMenuId(String roleId, String menuId);
}
