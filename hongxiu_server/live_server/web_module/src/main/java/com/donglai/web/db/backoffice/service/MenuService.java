package com.donglai.web.db.backoffice.service;

import com.donglai.web.constant.MenuType;
import com.donglai.web.db.backoffice.entity.Menu;
import com.donglai.web.db.backoffice.entity.Role;
import com.donglai.web.db.backoffice.repository.MenuRepository;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuService {
    @Autowired
    MenuRepository menuRepository;
    @Autowired
    MongoTemplate mongoTemplate;

    public Menu findByPath(String path) {
        return menuRepository.findByPath(path);
    }

    public Menu save(Menu menu) {
        return menuRepository.save(menu);
    }

    public List<Menu> getAllMenu() {
        return menuRepository.findAll();
    }

    public List<Menu> saveAll(List<Menu> menus) {
        return menuRepository.saveAll(menus);
    }

    public List<Menu> findAllByMenuType(MenuType menuType) {
        return menuRepository.findAllByMenuType(menuType);
    }

    public List<Menu> findMenusByMenuTypeAndMenuRole(MenuType menuType, List<Role> roles) {
        Criteria criteria = Criteria.where("menuType").is(menuType).orOperator(Criteria.where("roles").is(Lists.newArrayList()), Criteria.where("roles").is(roles));
        return mongoTemplate.find(Query.query(criteria), Menu.class);
    }

    public void deleteAllByMenuType(MenuType menuType) {
        menuRepository.deleteByMenuType(menuType);
    }
}
