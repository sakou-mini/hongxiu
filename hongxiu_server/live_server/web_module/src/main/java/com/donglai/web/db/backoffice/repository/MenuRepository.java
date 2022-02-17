package com.donglai.web.db.backoffice.repository;

import com.donglai.web.constant.MenuType;
import com.donglai.web.db.backoffice.entity.Menu;
import com.donglai.web.db.backoffice.entity.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MenuRepository extends MongoRepository<Menu, Long> {
    Menu findByPath(String path);

    List<Menu> findAllByMenuType(MenuType menuType);

    List<Menu> findAllByMenuTypeAndRolesIs(MenuType menuType, List<Role> roles);

    void deleteByMenuType(MenuType menuType);

}
