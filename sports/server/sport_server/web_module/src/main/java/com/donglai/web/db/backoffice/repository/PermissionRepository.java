package com.donglai.web.db.backoffice.repository;

import com.donglai.web.db.backoffice.entity.Permission;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;

public interface PermissionRepository extends MongoRepository<Permission,String> {
    List<Permission> findByIdIn(Collection<String> ids);
}
