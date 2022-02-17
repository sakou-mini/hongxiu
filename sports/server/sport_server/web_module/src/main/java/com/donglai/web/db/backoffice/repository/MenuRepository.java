package com.donglai.web.db.backoffice.repository;

import com.donglai.web.constant.MenuType;
import com.donglai.web.db.backoffice.entity.Menu;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MenuRepository extends MongoRepository<Menu,Long> {
    List<Menu> findByPath(String path);

    List<Menu> findAllByMenuType(MenuType menuType);

    void deleteByMenuType(MenuType menuType);

    List<Menu> findAllByIdIn(List<String> ids);

}
