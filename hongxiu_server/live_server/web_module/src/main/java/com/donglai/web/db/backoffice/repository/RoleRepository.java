package com.donglai.web.db.backoffice.repository;

import com.donglai.web.db.backoffice.entity.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RoleRepository extends MongoRepository<Role, String> {
    Role findByName(String name);

    List<Role> findByIdIn(List<String> ids);

    List<Role> deleteByIdIn(List<String> ids);
}
