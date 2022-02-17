package com.donglai.web.db.backoffice.repository;

import com.donglai.web.db.backoffice.entity.BackOfficeUser;
import com.donglai.web.db.backoffice.entity.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BackOfficeUserRepository extends MongoRepository<BackOfficeUser,String> {
    BackOfficeUser findByUsername(String username);

    List<BackOfficeUser> findByIdIn(List<String> ids);

    List<BackOfficeUser> findAllByRolesContains(List<Role> roles);
}
